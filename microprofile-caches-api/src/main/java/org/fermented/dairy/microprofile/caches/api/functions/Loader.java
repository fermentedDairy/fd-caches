package org.fermented.dairy.microprofile.caches.api.functions;

/**
 * Functional interface to allow for loader lambdas that throw checked exceptions
 * @param <P> Parameter type
 * @param <V> Value type
 */
@FunctionalInterface
public interface Loader<P, V> {

    /**
     * loads the value based on the provided key
     * @param key
     * @return loaded value
     * @throws Exception
     */
    V load(P key) throws Exception;
}
