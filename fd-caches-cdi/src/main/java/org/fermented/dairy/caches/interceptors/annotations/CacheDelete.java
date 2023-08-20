package org.fermented.dairy.caches.interceptors.annotations;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method should remove the entry mapped by the key from the cache.
 * This should be used when deleting the entity from the underlying source.
 */
@InterceptorBinding
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheDelete {
}
