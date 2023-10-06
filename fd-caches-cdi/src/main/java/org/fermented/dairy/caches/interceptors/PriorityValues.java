package org.fermented.dairy.caches.interceptors;

public final class PriorityValues {

    public static final int LOAD_INTERCEPTOR_PRIORITY = Integer.MAX_VALUE;
    public static final int DELETE_INTERCEPTOR_PRIORITY = LOAD_INTERCEPTOR_PRIORITY - 1;

    private PriorityValues(){}

}
