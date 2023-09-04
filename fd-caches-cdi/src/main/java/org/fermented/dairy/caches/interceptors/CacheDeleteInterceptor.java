package org.fermented.dairy.caches.interceptors;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.fermented.dairy.caches.interceptors.annotations.CacheDelete;
import org.fermented.dairy.caches.interceptors.annotations.CacheLoad;

/**
 * Interceptor for deleting caches. Caches are deleted BEFORE the intercepted method is called.
 * Can be used with the {@link CacheLoad} annotation (using parameters for the keys) to update a value in cache.
 */
@Interceptor
@CacheDelete
@Dependent
@Priority(Integer.MAX_VALUE - 1)
public class CacheDeleteInterceptor extends AbstractCacheInterceptor {

    /**
     * Interceptor method invoked around the target method (annotated with {@link CacheDelete}).
     *
     * @param ctx The InvocationContext of the intercepted method
     *
     * @return the result of {@code ctx.proceed()}
     *
     * @throws Exception The exception thrown by the {@code ctx.proceed()} call
     */
    @AroundInvoke
    public Object deleteFromCache(final InvocationContext ctx) throws Exception {
        Object result = ctx.proceed();
        return result;
    }
}
