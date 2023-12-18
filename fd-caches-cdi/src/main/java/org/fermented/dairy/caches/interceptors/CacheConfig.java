package org.fermented.dairy.caches.interceptors;

import java.util.Optional;

/**
 * Interface to decouple cache handler abstract class from microprofile caching.
 */
public interface CacheConfig {
    <T> Optional<T> getOptionalValue(String s, Class<T> klass);
}
