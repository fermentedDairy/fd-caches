package org.fermented.dairy.caches.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.fermented.dairy.caches.interceptors.annotations.CacheLoad;

@Interceptor
@CacheLoad
public class CacheLoadInterceptor {

    @AroundInvoke
    public Object loadIntoCache(final InvocationContext ctx) throws Exception {
        return null;
    }
}
