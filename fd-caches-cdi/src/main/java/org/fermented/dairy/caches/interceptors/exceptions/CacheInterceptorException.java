package org.fermented.dairy.caches.interceptors.exceptions;

public class CacheInterceptorException extends Exception {
    public CacheInterceptorException(final String message, final Object... params) {
        super(message.formatted(params));
    }
}
