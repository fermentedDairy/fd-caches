package org.fermented.dairy.caches.interceptors.exceptions;

public class CacheInterceptorRuntimeException extends RuntimeException {

    public CacheInterceptorRuntimeException(final String message, final Object... params) {
        super(message.formatted(params));
    }
}