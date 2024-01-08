package org.fermented.dairy.caches.api.exceptions;

/**
 * Runtime Exception thrown by the cache implementations.
 */
public class CacheException extends Exception {

    /**
     * Exception Constructor with a formatted message.
     *
     * @param messageTemplate The formatted message
     * @param params Params used for the formatted message
     *
     * @see  java.util.Formatter
     */
    public CacheException(final String messageTemplate, final Object... params) {
        super(messageTemplate.formatted(params));
    }

    /**
     * Exception Constructor with a causing exception.
     *
     * @param causedBy The wrapped causing exception
     */
    public CacheException(final Throwable causedBy) {
        super(causedBy);
    }
}
