package org.fermented.dairy.caches.api.functions;

/**
 * Functional interface to allow for loader lambdas that throw checked exceptions.
 *
 * @param <P> Parameter type
 * @param <V> Value type
 */
@FunctionalInterface
public interface Loader<P, V> {

    /**
     * loads the value based on the provided key.
     *
     * @param param The parameter used to load the value
     *
     * @return loaded value
     *
     * @throws Exception The exception thrown by the loading lambda
     */
    V load(P param) throws Exception;
}
