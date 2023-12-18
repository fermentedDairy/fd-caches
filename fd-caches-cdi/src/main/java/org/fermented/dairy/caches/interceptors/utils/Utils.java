package org.fermented.dairy.caches.interceptors.utils;

import jakarta.enterprise.inject.Instance;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;

/**
 * Utils class for Interceptors.
 */
public final class Utils {

    private Utils() {}

    /**
     * Constructs a map of cache names to {@link CacheProvider CacheProviders}.
     *
     * @param providers Instance containing provider instances.
     *
     * @return map of cache names to {@link CacheProvider CacheProviders}.
     */
    public static Map<String, CacheProvider> initCacheNameMap(final Instance<CacheProvider> providers) {
        return providers.stream().collect(Collectors.toMap(
                CacheProvider::getProviderName,
                Function.identity()
        ));
    }
}
