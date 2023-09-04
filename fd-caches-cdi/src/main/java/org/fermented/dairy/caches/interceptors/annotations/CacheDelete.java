package org.fermented.dairy.caches.interceptors.annotations;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method should remove the entry mapped by the key from the cache.
 * This should be used when deleting the entity from the underlying source.
 * Can be used with the {@link CacheLoad} annotation (using parameters for the keys) to update a value in cache provided {@link #deleteFirst()} is true.
 */
@InterceptorBinding
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheDelete {

    /**
     * If true, the cache is deleted first. If false, cache is deleted after the intercepted method is called.
     *
     * @return true if it must delete before executing the intercepted method
     */
    boolean deleteFirst() default true;

    /**
     * If true, searches the result of intercepted method for the cache key (field annotated with {@link CacheKey}).
     *
     * @return true if the cache key is in the result object.
     */
    boolean useResultForKey() default false;
}
