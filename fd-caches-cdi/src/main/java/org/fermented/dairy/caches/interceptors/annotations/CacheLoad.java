package org.fermented.dairy.caches.interceptors.annotations;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method should load the result from the cache in the case of a cache hit or load into the cache
 * in the case of a cache miss and return result.
 */
@InterceptorBinding
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheLoad {
}
