package org.fermented.dairy.caches.interceptors.annotations;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method should load the result from the cache in the case of a cache hit or load into the cache
 * in the case of a cache miss and return result.
 * Can be used with the {@link CacheDelete} annotation (using parameters for the keys) to update a value in cache.
 */
@InterceptorBinding
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheLoad {

    /**
     * The cached class Class object. Required if the intercepted method returns an Optional.
     *
     * @return The cached class Class object.
     */
    Class<?> optionalClass() default Void.class;
}
