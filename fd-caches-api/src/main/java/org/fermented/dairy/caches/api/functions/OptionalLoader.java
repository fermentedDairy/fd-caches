package org.fermented.dairy.caches.api.functions;

import java.util.Optional;

/**
 * A loader the returns Optional see {@link Loader}.
 *
 * @param <P> Parameter type
 * @param <V> Value type
 */
@FunctionalInterface
public interface OptionalLoader<P, V> extends Loader<P, Optional<V>> {

}
