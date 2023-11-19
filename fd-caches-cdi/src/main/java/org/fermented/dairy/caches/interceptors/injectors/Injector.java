package org.fermented.dairy.caches.interceptors.injectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.fermented.dairy.caches.providers.HashMapCacheProvider;

/**
 * Utility holding producer methods for non CDI Cache beans.
 */
@ApplicationScoped
public class Injector {

    /**
     * Produces HashMapCache.
     *
     * @return HashMapCache
     */
    @ApplicationScoped
    @Produces
    public HashMapCacheProvider hashMapCache() {
        return new HashMapCacheProvider();
    }



}
