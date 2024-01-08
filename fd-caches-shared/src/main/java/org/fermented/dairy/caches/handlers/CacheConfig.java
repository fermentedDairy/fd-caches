package org.fermented.dairy.caches.handlers;

import java.util.Optional;

/**
 * Interface to decouple cache handler abstract class from microprofile caching.
 */
public interface CacheConfig {
    <T> Optional<T> getOptionalValue(String s, Class<T> klass);
}
