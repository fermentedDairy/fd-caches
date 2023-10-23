package org.fermented.dairy.caches.interceptors.injectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.fermented.dairy.caches.providers.HashMapCache;

/**
 * Utility holding producer methods for non CDI Cache beans.
 */
@ApplicationScoped
public class Injector {

    @Inject
    @ConfigProperty(name = "fd.caches.provider.default", defaultValue = "internal.default.cache")
    private String defaultProviderName;

    /**
     * Produces HashMapCache.
     *
     * @return HashMapCache
     */
    @ApplicationScoped
    @Produces
    public HashMapCache hashMapCache() {
        return new HashMapCache();
    }



}
