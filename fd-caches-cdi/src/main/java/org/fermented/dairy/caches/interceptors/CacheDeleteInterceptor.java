package org.fermented.dairy.caches.interceptors;

import static org.fermented.dairy.caches.interceptors.PriorityValues.DELETE_INTERCEPTOR_PRIORITY;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.eclipse.microprofile.config.Config;
import org.fermented.dairy.caches.annotations.CacheDelete;
import org.fermented.dairy.caches.annotations.CacheLoad;
import org.fermented.dairy.caches.annotations.Cached;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.fermented.dairy.caches.handlers.AbstractCacheHandler;


/**
 * Interceptor for deleting caches. Caches are deleted BEFORE the intercepted method is called.
 * Can be used with the {@link CacheLoad} annotation (using parameters for the keys) to update a value in cache.
 */
@Interceptor
@CacheDelete
@Dependent
@Priority(DELETE_INTERCEPTOR_PRIORITY)
public class CacheDeleteInterceptor extends AbstractCacheHandler {

    /**
     * CDI compliant constructor.
     *
     * @param config Config
     * @param providers Injected CDI cache providers
     */
    @Inject
    public CacheDeleteInterceptor(final Config config, final Instance<CacheProvider> providers) {
        super(MicroProfileCacheConfig.using(config), providers);
    }


    /**
     * Interceptor method invoked around the target method (annotated with {@link CacheDelete}).
     *
     * @param ctx The InvocationContext of the intercepted method
     * @return the result of {@code ctx.proceed()}
     * @throws Exception The exception thrown by the {@code ctx.proceed()} call
     */
    @AroundInvoke
    public Object deleteFromCache(final InvocationContext ctx) throws Exception {

        final CacheProvider cacheProvider = getCacheForDelete(ctx.getMethod());
        final String cacheName = getCacheNameForDelete(ctx.getMethod());
        Object key = getCacheKey(ctx.getMethod(), ctx.getParameters());
        if (key.getClass().isAnnotationPresent(Cached.class)) {
            key = getKeyFromCachedClass(key);
        }

        cacheProvider.removeValue(cacheName, key);
        return ctx.proceed();
    }


}
