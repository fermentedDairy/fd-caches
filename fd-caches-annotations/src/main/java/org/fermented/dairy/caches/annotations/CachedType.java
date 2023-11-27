package org.fermented.dairy.caches.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the type of the cached class.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedType {

    /**
     * The cached class Class object. Required if the intercepted method returns an Optional.
     *
     * @return The cached class Class object.
     */
    Class<?> value();
}
