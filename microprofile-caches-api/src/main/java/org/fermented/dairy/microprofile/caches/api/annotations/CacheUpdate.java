package org.fermented.dairy.microprofile.caches.api.annotations;

import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the result of the annotated method should replace the cached value. This should be used when updating the entity in the underlying source.
 */
@InterceptorBinding
@Target( { METHOD } )
@Retention( RUNTIME )
public @interface CacheUpdate {
}
