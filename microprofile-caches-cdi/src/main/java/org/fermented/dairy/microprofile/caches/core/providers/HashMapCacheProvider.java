package org.fermented.dairy.microprofile.caches.core.providers;

import org.fermented.dairy.microprofile.caches.api.functions.Loader;
import org.fermented.dairy.microprofile.caches.api.functions.OptionalLoader;
import org.fermented.dairy.microprofile.caches.api.interfaces.CacheProvider;

import java.util.Collection;

/**
 * A
 */
public final class HashMapCacheProvider implements CacheProvider {

    @Override
    public Object load(final Object key, final Loader<Object, Object> loader, final String cacheName, final long ttl, final Class keyClass, final Class valueClass) {
        return null;
    }

    @Override
    public Object load(final Object key, final OptionalLoader<Object, Object> loader, final String cacheName, final long ttl, final Class keyClass, final Class valueClass) {
        return null;
    }

    @Override
    public void removeValue(final String cacheName, final Object key) {

    }

    @Override
    public void clearCache(final String cacheName) {

    }

    @Override
    public Collection<String> getCacheNames() {
        return null;
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
