package org.fermented.dairy.microprofile.caches.api.exceptions;

/**
 * Runtime Exception thrown by the cache implementations
 */
public class CacheException extends RuntimeException{

    public CacheException(String message, Object... params) {
        super(String.format(message, params));
    }
}
