package org.fermented.dairy.caches.sb.aop.rest.controller.providers;

import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.functions.OptionalLoader;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cache provider for testing cache configuration.
 */
@Component
public class ConfiguredCacheProvider implements CacheProvider {

    private static final ConcurrentHashMap<KeyHolder, ValueHolder> CACHE_MAP = new ConcurrentHashMap<>();

    private static final Set<String> CACHE_NAMES = new HashSet<>();

    @Override
    public Object load(
            final Object key,
            final Loader<Object, Object> loader,
            final String cacheName,
            final long ttlMilliSeconds,
            final Class keyClass,
            final Class valueClass) throws Exception {
        final ValueHolder holder = CACHE_MAP.computeIfAbsent(
                new KeyHolder(cacheName, key),
                kh -> {
                    try {
                        return new ValueHolder(
                                loader.load(key),
                                System.currentTimeMillis() + ttlMilliSeconds
                        );
                    } catch (final Throwable exc) {
                        throw new RuntimeException(exc);
                    }
                }
        );
        if (holder.expiryTime() < System.currentTimeMillis()) {
            return null;
        }
        CACHE_NAMES.add(cacheName);
        return holder.value();
    }

    @Override
    public Object load(
            final Object key,
            final Object value,
            final String cacheName,
            final long ttlMilliSeconds,
            final Class keyClass,
            final Class valueClass) throws Exception {
        final ValueHolder holder = CACHE_MAP.computeIfAbsent(
                new KeyHolder(cacheName, key),
                kh -> new ValueHolder(
                        value,
                        System.currentTimeMillis() + ttlMilliSeconds
                )
        );
        if (holder.expiryTime() < System.currentTimeMillis()) {
            return null;
        }
        CACHE_NAMES.add(cacheName);
        return holder.value();
    }

    @Override
    public Optional<Object> loadOptional(
            final Object key,
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
                            //noinspection OptionalAssignedToNull
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
        CACHE_MAP.clear();
        CACHE_NAMES.clear();
    }

    @Override
    public void removeValue(final String cacheName, final Object key) {
        CACHE_MAP.remove(new KeyHolder(cacheName, key));
    }

    @Override
    public void clearCache(final String cacheName) {
        CACHE_MAP.keySet().stream().filter(
                key -> key.cacheName().equals(cacheName)
        ).forEach(CACHE_MAP::remove);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Stream.concat(CACHE_MAP.keySet().stream().map(KeyHolder::cacheName),
                CACHE_NAMES.stream()
        ).collect(Collectors.toSet());
    }

    @Override
    public Collection<Object> getKeys(final String cacheName) {
        return CACHE_MAP.keySet().stream().filter(
                        key -> key.cacheName().equals(cacheName)
                ).map(KeyHolder::key)
                .collect(Collectors.toSet());
    }

    @Override
    public String getProviderName() {
        return "configuredCache";
    }

    @Override
    public Optional<Object> peek(final String cacheName, final Object key) {
        return Optional.ofNullable(CACHE_MAP.get(
                new KeyHolder(cacheName, key)
        ).value());
    }

    private record KeyHolder(String cacheName, Object key){}

    private record ValueHolder(Object value, long expiryTime){}
}
