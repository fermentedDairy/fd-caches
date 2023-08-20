package org.fermented.dairy.caches.interceptors.annotations;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicates that the result of the annotated method should replace the cached value.
 * This should be used when updating the entity in the underlying source.
 */
@InterceptorBinding
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheUpdate {
}
