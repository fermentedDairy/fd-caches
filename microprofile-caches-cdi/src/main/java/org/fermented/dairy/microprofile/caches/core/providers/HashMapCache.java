package org.fermented.dairy.microprofile.caches.core.providers;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.fermented.dairy.microprofile.caches.api.exceptions.CacheException;
import org.fermented.dairy.microprofile.caches.api.functions.Loader;
import org.fermented.dairy.microprofile.caches.api.functions.OptionalLoader;
import org.fermented.dairy.microprofile.caches.api.interfaces.Cache;

/**
 * A hashmap based cache provider
 *
 * @noinspection rawtypes
 */
public final class HashMapCache implements Cache {

    private static final ConcurrentHashMap<String, CacheHolder> CACHES = new ConcurrentHashMap<>();//NOSONAR: java3740

    @Override
    public Object load(final Object key, final Loader<Object, Object> loader, final String cacheName, final long ttl, final Class keyClass, final Class valueClass) throws Exception {
        final CacheHolder cacheHolder = getCache(cacheName, keyClass, valueClass);

        validateKeyClass(key, keyClass, cacheHolder, cacheName);

        final ConcurrentHashMap<Object, SoftReference<CacheEntry>> cache = cacheHolder.cache();
        SoftReference<CacheEntry> entryReference;
        if ((entryReference = cache.get(key)) == null //mapped value is null
                || entryReference.get() == null //SoftReference that's mapped has been GCed
                || entryReference.get().isExpired()) //cached value has expired
        {

            entryReference = new SoftReference<>(
                    new CacheEntry(ttl)
            );
            return loadValueIntoCache(key, loader, cacheName, valueClass, cacheHolder, cache, entryReference);
        }
        entryReference.get().readLock().lock();
        try {
            return entryReference.get().getValue();
        } finally {
            entryReference.get().readLock().unlock();
        }
    }

    @Override
    public Object loadOptional(final Object key, final OptionalLoader<Object, Object> loader, final String cacheName, final long ttl, final Class keyClass, final Class valueClass) throws Exception {
        return null;
    }

    @Override
    public void purge() {
        CACHES.clear();
    }

    @Override
    public void removeValue(final String cacheName, final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearCache(final String cacheName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getCacheNames() {
        return CACHES.keySet();
    }

    @Override
    public Collection<Object> getKeys(final String cacheNames) {
        final CacheHolder cacheHolder;

        if ((cacheHolder = CACHES.get(cacheNames)) == null) return Collections.emptySet();

        final ConcurrentHashMap<Object, SoftReference<CacheEntry>> cache = cacheHolder.cache();
        cache.entrySet().stream()
                .filter(
                        entry -> entry.getValue().get() == null ||
                                entry.getValue().get().isExpired()
                ).forEach(entry -> cache.remove(entry.getKey()));
        return cache.keySet();
    }

    @Override
    public String getProviderName() {
        return getClass().getCanonicalName();
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

    private Object loadValueIntoCache(final Object key, final Loader<Object, Object> loader, final String cacheName, final Class valueClass, final CacheHolder cacheHolder, final ConcurrentHashMap<Object, SoftReference<CacheEntry>> cache, final SoftReference<CacheEntry> entryReference) throws Exception {
        entryReference.get().writeLock().lock();
        try {
            if (entryReference.get().getValue() != null)
                return entryReference.get().getValue();
            cache.put(key, entryReference);
            final Object value;
            if ((value = loader.load(key)) == null) {
                cache.remove(key);
                return null;
            }
            validateResultClass(value, valueClass, cacheHolder, cacheName);
            entryReference.get().setValue(loader.load(key));
            return entryReference.get().getValue();
        } finally {
            entryReference.get().writeLock().unlock();
        }
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
            if (this.value != null) throw new CacheException("values can only be set once");
            expiryTime = System.currentTimeMillis() + ttl; //refresh expiry time after value has been set
            this.value = value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final CacheEntry that = (CacheEntry) o;
            return expiryTime == that.expiryTime && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expiryTime, value);
        }

        @Override
        public String toString() {
            return "CacheEntry{" +
                    "expiryTime=" + expiryTime +
                    ", currentTime=" + System.currentTimeMillis() +
                    ", isExpired=" + isExpired() +
                    ", value=" + value +
                    '}';
        }
    }

    private record CacheHolder(
            ConcurrentHashMap<Object, SoftReference<CacheEntry>> cache,
            Class keyClass, Class resultClass) {

    }
}
