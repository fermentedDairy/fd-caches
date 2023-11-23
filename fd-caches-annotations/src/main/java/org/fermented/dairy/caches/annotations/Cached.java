package org.fermented.dairy.caches.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation that indicates that a class is cached. Provided class specific defaults for cache name and ttl.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface Cached {

    /**
     * Default TTL to indicate an unset value.
     */
    long DEFAULT_TTL = -1L;

    /**
     * The cache provider name to use as the default for this type.
     * If set to empty String, then the interceptor should use either the value from the config or the default.
     *
     * @return the cache name.
     */
    String cacheProviderName() default "";

    /**
     * The cache name to use as the default for this type.
     * If set to empty String, then the interceptor should use either the value from the config or the default.
     *
     * @return the cache name.
     */
    String cacheName() default "";

    /**
     * The time to live (in milliseconds) to use as the default for this type.
     * If set to the default, then the interceptor should use either the value from the config or the default.
     *
     * @return the time to live for the cached objects of this type (in milliseconds).
     */
    long ttlMilliSeconds() default DEFAULT_TTL;
}
