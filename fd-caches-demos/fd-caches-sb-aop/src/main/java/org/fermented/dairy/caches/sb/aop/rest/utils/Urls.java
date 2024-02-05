package org.fermented.dairy.caches.sb.aop.rest.utils;

/**
 * URL utilities.
 */
public final class Urls {

    public static final String CONTEXT_ROOT = "fd-caches-sb-aop";

    public static final String APP_ROOT = "api";

    public static final String DATA_ROOT = "data";

    private Urls() {}

    /**
     * Generate URL from fragments.
     *
     * @param parts the fragments to assemble into a URL.
     * @return assembled URL
     */
    public static String generateUrlFromParts(final String... parts) {
        return String.join("/", parts);
    }

}
