package org.fermented.dairy.caches.interceptors;

import java.util.Optional;
import org.eclipse.microprofile.config.Config;

/**
 * Microprofile Cache wrapper implementation of {@link CacheConfig}.
 */
public class MicroProfileCacheConfig implements CacheConfig {

    private final Config config;

    private MicroProfileCacheConfig(final Config config) {
        this.config = config;
    }

    @Override
    public <T> Optional<T> getOptionalValue(final String s, final Class<T> klass) {
        return config.getOptionalValue(s, klass);
    }

    public static CacheConfig using(final Config config) {
        return new MicroProfileCacheConfig(config);
    }
}
