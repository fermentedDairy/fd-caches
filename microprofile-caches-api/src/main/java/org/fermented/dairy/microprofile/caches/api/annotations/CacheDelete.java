package org.fermented.dairy.microprofile.caches.api.annotations;

import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the annotated method should remove the entry mapped by the key from the cache. This should be used when deleting the entity from the underlying source
 */
@InterceptorBinding
@Target( { METHOD } )
@Retention( RUNTIME )
public @interface CacheDelete {
}
