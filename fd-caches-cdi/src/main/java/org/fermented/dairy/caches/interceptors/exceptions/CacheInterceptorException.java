package org.fermented.dairy.caches.interceptors.exceptions;

/**
 * Checked exception for interceptors.
 */
public class CacheInterceptorException extends Exception {
    public CacheInterceptorException(final String message, final Object... params) {
        super(message.formatted(params));
    }

    public CacheInterceptorException(final Throwable causedBy) {
        super(causedBy);
    }
}
