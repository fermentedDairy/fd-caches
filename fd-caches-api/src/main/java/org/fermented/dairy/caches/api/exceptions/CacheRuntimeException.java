package org.fermented.dairy.caches.api.exceptions;

/**
 * Runtime Exception thrown by the cache implementations.
 */
public class CacheRuntimeException extends RuntimeException {

    /**
     * Exception Constructor with a formatted message.
     *
     * @param messageTemplate The formatted message
     * @param params Params used for the formatted message
     *
     * @see  java.util.Formatter
     */
    public CacheRuntimeException(final String messageTemplate, final Object... params) {
        super(String.format(messageTemplate, params));
    }
}
