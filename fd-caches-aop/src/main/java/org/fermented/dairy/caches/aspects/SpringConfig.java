package org.fermented.dairy.caches.aspects;

import java.util.Optional;
import org.fermented.dairy.caches.handlers.CacheConfig;
import org.springframework.core.env.Environment;

/**
 * Spring Cache wrapper implementation of {@link CacheConfig}.
 */
public class SpringConfig implements CacheConfig {

    private final Environment environment;

    private SpringConfig(final Environment environment) {
        this.environment = environment;
    }

    @Override
    public <T> Optional<T> getOptionalValue(final String key, final Class<T> klass) {
        return Optional.ofNullable(environment.getProperty(key, klass));
    }

    public static CacheConfig using(final Environment environment) {
        return new SpringConfig(environment);
    }
}
