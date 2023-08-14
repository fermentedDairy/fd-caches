package org.fermented.dairy.microprofile.caches.api.exceptions;

/**
 * Runtime Exception thrown by the cache implementations.
 */
public class CacheException extends RuntimeException {

    /**
     * Exception Constructor with a formatted message.
     *
     * @param message The formatted message
     * @param params Params used for the formatted message
     *
     * @see  java.util.Formatter
     */
    public CacheException(final String message, final Object... params) {
        super(String.format(message, params));
    }
}
