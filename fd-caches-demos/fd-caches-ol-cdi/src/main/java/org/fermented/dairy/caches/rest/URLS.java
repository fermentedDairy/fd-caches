package org.fermented.dairy.caches.rest;

public final class URLS {

    public static final String CONTEXT_ROOT = "fd-caches-ol-cdi";

    public static final String APP_ROOT = "api";

    public static final String DATA_ROOT = "data";

    private URLS() {}

    public static String generateURLfromParts(final String... parts) {
        return String.join("/", parts);
    }

}
