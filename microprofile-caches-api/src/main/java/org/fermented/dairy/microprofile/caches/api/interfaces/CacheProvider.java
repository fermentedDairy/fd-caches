package org.fermented.dairy.microprofile.caches.api.interfaces;

import org.fermented.dairy.microprofile.caches.api.functions.Loader;
import org.fermented.dairy.microprofile.caches.api.functions.OptionalLoader;

import java.util.Collection;

/**
 * Cache Provider Interface
 */
public interface CacheProvider {

    Object load(Object key, Loader<Object, Object> loader, String cacheName, long ttl, Class keyClass, Class valueClass);

    Object load(Object key, OptionalLoader<Object, Object> loader, String cacheName, long ttl, Class keyClass, Class valueClass);

    void removeValue(String cacheName, Object key);

    void clearCache(String cacheName);

    Collection<String> getCacheNames();

    Collection<Object> getKeys(String cacheNames);

    String getProviderName();
}
