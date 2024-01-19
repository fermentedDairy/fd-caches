package org.fermented.dairy.caches.api.functions;

@FunctionalInterface
public interface Proceeder<T> {
    T proceed() throws Throwable; //NOSONAR: java:S112 - Must be throwable to comply with JoinPoint signature
}
