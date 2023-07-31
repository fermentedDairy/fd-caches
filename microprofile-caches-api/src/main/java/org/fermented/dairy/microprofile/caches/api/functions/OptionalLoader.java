package org.fermented.dairy.microprofile.caches.api.functions;

import java.util.Optional;

/**
 * A loader the returns Optional see {@link Loader}
 * @param <P>
 * @param <V>
 */
@FunctionalInterface
public interface OptionalLoader<P, V> extends Loader<P, Optional<V>>{
}
