package org.fermented.dairy.caches.api.functions;


import java.util.function.Supplier;

@FunctionalInterface
public interface Proceeder<T> {
    T proceed() throws Throwable;
}
