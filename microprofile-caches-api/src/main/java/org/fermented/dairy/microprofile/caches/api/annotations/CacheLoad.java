package org.fermented.dairy.microprofile.caches.api.annotations;

import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the annotated method should load the result from the cache in the case of a cache hit or load into the cache in the case of a cache miss and return result.
 */
@InterceptorBinding
@Target( { METHOD } )
@Retention( RUNTIME )
public @interface CacheLoad {
}
