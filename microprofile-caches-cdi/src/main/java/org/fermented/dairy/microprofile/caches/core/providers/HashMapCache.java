package org.fermented.dairy.microprofile.caches.core.providers;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.fermented.dairy.microprofile.caches.api.functions.Loader;
import org.fermented.dairy.microprofile.caches.api.functions.OptionalLoader;
import org.fermented.dairy.microprofile.caches.api.interfaces.Cache;

/**
 * A hashmap based cache provider
 *
 * @noinspection rawtypes
 */
public final class HashMapCache implements Cache {

    private static final ConcurrentHashMap<String, ConcurrentHashMap> CACHES = new ConcurrentHashMap<>();//NOSONAR: java3740

    @Override
    public Object load(final Object key, final Loader<Object, Object> loader, final String cacheName, final long ttl, final Class keyClass, final Class valueClass) {
        return null;
    }

    @Override
    public Object loadOptional(final Object key, final OptionalLoader<Object, Object> loader, final String cacheName, final long ttl, final Class keyClass, final Class valueClass) {
        return null;
    }

    @Override
    public void purge() {
        CACHES.values().forEach(ConcurrentHashMap::clear);
    }

    @Override
    public void removeValue(final String cacheName, final Object key) {

    }

    @Override
    public void clearCache(final String cacheName) {

    }

    @Override
    public Collection<String> getCacheNames() {
        return CACHES.keySet();
    }

    @Override
    public Collection<Object> getKeys(final String cacheNames) {
        return null;
    }

    @Override
    public String getProviderName() {
        return null;
    }
}
