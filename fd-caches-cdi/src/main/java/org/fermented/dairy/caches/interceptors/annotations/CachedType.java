package org.fermented.dairy.caches.interceptors.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
