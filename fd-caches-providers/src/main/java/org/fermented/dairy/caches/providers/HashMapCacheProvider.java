package org.fermented.dairy.caches.providers;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.fermented.dairy.caches.api.exceptions.CacheException;
import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.functions.OptionalLoader;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;

/**
 * A hashmap based cache provider.
 *
 * @noinspection rawtypes
 */
public class HashMapCacheProvider implements CacheProvider {

    private static final ConcurrentHashMap<String, CacheHolder> CACHES = new ConcurrentHashMap<>(); //NOSONAR: java3740

    @Override
    public Object load(final Object key,
                       final Loader<Object, Object> loader,
                       final String cacheName,
                       final long ttlMilliSeconds,
                       final Class keyClass,
                       final Class valueClass) throws Exception {
        final CacheHolder cacheHolder = getCache(cacheName, keyClass, valueClass);

        validateKeyClass(key, keyClass, cacheHolder, cacheName);

        final ConcurrentHashMap<Object, SoftReference<CacheEntry>> cache = cacheHolder.cache();
        SoftReference<CacheEntry> entryReference;
        //noinspection DataFlowIssue
        if ((entryReference = cache.get(key)) == null //mapped value is null
                //SoftReference that's mapped has been GCed
                || entryReference.get() == null
                //cached value has expired
                || entryReference.get().isExpired()) {
            entryReference = new SoftReference<>(
                    new CacheEntry(ttlMilliSeconds)
            );
            return loadValueIntoCache(key, loader, cacheName, valueClass, cacheHolder, cache, entryReference);
        }
        //noinspection DataFlowIssue
        entryReference.get().readLock().lock();
        try {
            //noinspection DataFlowIssue
            return entryReference.get().getValue();
        } finally {
            //noinspection DataFlowIssue
            entryReference.get().readLock().unlock();
        }
    }

    @Override
    public Object load(final Object key,
                       final Object value,
                       final String cacheName,
                       final long ttlMilliSeconds,
                       final Class keyClass,
                       final Class valueClass) throws Exception {
        return load(key, k -> value, cacheName, ttlMilliSeconds, keyClass, valueClass);
    }

    @Override
    public Optional loadOptional(final Object key,
                                 final OptionalLoader<Object, Object> loader,
                                 final String cacheName,
                                 final long ttlMilliSeconds,
                                 final Class keyClass,
                                 final Class valueClass) throws Exception {
        return Optional.ofNullable(
                load(
                        key,
                        k -> {
                            final Optional<Object> val = loader.load(k);
                            return val == null ? null : val.orElse(null); //NOSONAR: java:S2789
                        },
                        cacheName,
                        ttlMilliSeconds,
                        keyClass,
                        valueClass
                ));
    }

    @Override
    public void purge() {
        CACHES.clear();
    }

    @Override
    public void removeValue(final String cacheName, final Object key) {
        final CacheHolder cacheHolder;
        if ((cacheHolder = CACHES.get(cacheName)) != null) {
            cacheHolder.cache().remove(key);
        }
    }

    @Override
    public void clearCache(final String cacheName) {
        final CacheHolder cacheHolder;
        if ((cacheHolder = CACHES.get(cacheName)) != null) {
            cacheHolder.cache().clear();
        }
    }

    @Override
    public Collection<String> getCacheNames() {
        return CACHES.keySet();
    }

    @Override
    public Collection<Object> getKeys(final String cacheName) {
        final CacheHolder cacheHolder;

        if ((cacheHolder = CACHES.get(cacheName)) == null) {
            return Collections.emptySet();
        }

        final ConcurrentHashMap<Object, SoftReference<CacheEntry>> cache = cacheHolder.cache();
        for (final Map.Entry<Object, SoftReference<CacheEntry>> entry : cache.entrySet()) {
            //noinspection DataFlowIssue
            if (entry.getValue().get() == null
                    || entry.getValue().get().isExpired()) {
                cache.remove(entry.getKey());
            }
        }
        return cache.keySet();
    }

    private CacheHolder getCache(final String cacheName, final Class keyClass, final Class resultClass) {
        return CACHES.computeIfAbsent(cacheName, key -> new CacheHolder(
                new ConcurrentHashMap<>(),
                keyClass,
                resultClass
        ));
    }

    private void validateKeyClass(final Object key, final Class keyClass, final CacheHolder cacheHolder, final String cacheName) {
        final Object nonNullKey = Objects.requireNonNull(key, "the key cannot be null");
        if (!keyClass.isInstance(nonNullKey) || !cacheHolder.keyClass().isInstance(nonNullKey)) {
            throw new CacheException("%s is not a valid key class for cache %s", nonNullKey.getClass().getCanonicalName(), cacheName);
        }
    }

    private void validateResultClass(final Object result, final Class resultClass, final CacheHolder cacheHolder, final String cacheName) {
        if (!resultClass.isInstance(result) || !cacheHolder.resultClass().isInstance(result)) {
            throw new CacheException("%s is not a valid result class for cache %s", result.getClass().getCanonicalName(), cacheName);
        }
    }

    private Object loadValueIntoCache(final Object key,
                                      final Loader<Object, Object> loader,
                                      final String cacheName,
                                      final Class valueClass,
                                      final CacheHolder cacheHolder,
                                      final ConcurrentHashMap<Object, SoftReference<CacheEntry>> cache,
                                      final SoftReference<CacheEntry> entryReference) throws Exception {
        //noinspection DataFlowIssue
        entryReference.get().writeLock().lock();
        try {
            //noinspection DataFlowIssue
            if (entryReference.get().getValue() != null) {
                //noinspection DataFlowIssue
                return entryReference.get().getValue();
            }
            cache.put(key, entryReference);
            final Object value;
            if ((value = loader.load(key)) == null) {
                cache.remove(key);
                return null;
            }
            validateResultClass(value, valueClass, cacheHolder, cacheName);
            //noinspection DataFlowIssue
            entryReference.get().setValue(loader.load(key));
            //noinspection DataFlowIssue
            return entryReference.get().getValue();
        } finally {
            //noinspection DataFlowIssue
            entryReference.get().writeLock().unlock();
        }
    }

    @Override
    public String getProviderName() {
        return "internal.default.cache";
    }

    @Override
    public Optional<Object> peek(final String cacheName, final Object key) {
        final CacheHolder cache;
        final SoftReference<CacheEntry> cacheReference;
        final CacheEntry cacheEntry;

        if ((cache = CACHES.get(cacheName)) == null
                || (cacheReference = cache.cache().get(key)) == null
                || (cacheEntry = cacheReference.get()) == null)
            return Optional.empty();
        return Optional.of(cacheEntry.getValue());
    }

    private static final class CacheEntry extends ReentrantReadWriteLock {
        private final long ttl;
        private long expiryTime;
        private Object value; //NOSONAR: java:S1948, not planning on any serialisation

        public CacheEntry(final long ttl) {
            this.ttl = ttl;
            expiryTime = System.currentTimeMillis() + ttl;
        }

        public boolean isExpired() {
            return expiryTime <= System.currentTimeMillis();
        }

        public Object getValue() {
            return value;
        }

        public void setValue(final Object value) {
            if (this.value != null) {
                throw new CacheException("values can only be set once");
            }
            expiryTime = System.currentTimeMillis() + ttl; //refresh expiry time after value has been set
            this.value = value;
        }
    }

    private record CacheHolder(
            ConcurrentHashMap<Object, SoftReference<CacheEntry>> cache,
            Class keyClass,
            Class resultClass) {


        private CacheHolder(
                final ConcurrentHashMap<Object, SoftReference<CacheEntry>> cache,
                final Class keyClass,
                final Class resultClass) {
            this.cache = Objects.requireNonNull(cache);
            this.keyClass = Objects.requireNonNull(keyClass);
            this.resultClass = Objects.requireNonNull(resultClass);
        }
    }
}
