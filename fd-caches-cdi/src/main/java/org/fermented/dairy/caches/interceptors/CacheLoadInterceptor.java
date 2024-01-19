package org.fermented.dairy.caches.interceptors;

import static org.fermented.dairy.caches.interceptors.PriorityValues.LOAD_INTERCEPTOR_PRIORITY;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;

import org.eclipse.microprofile.config.Config;
import org.fermented.dairy.caches.annotations.CacheLoad;
import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.functions.Proceeder;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.fermented.dairy.caches.handlers.AbstractCacheHandler;


/**
 * CDI caching interceptor.
 */
@Interceptor
@CacheLoad
@Dependent
@Priority(LOAD_INTERCEPTOR_PRIORITY)
public class CacheLoadInterceptor extends AbstractCacheHandler {

    /**
     * CDI compliant constructor.
     *
     * @param config Config
     * @param providers Injected CDI cache providers
     */
    @Inject
    public CacheLoadInterceptor(final Config config, final Instance<CacheProvider> providers) {
        super(MicroProfileCacheConfig.using(config),  providers);
    }

    /**
     * Interceptor method invoked around the target method (annotated with {@link CacheLoad}).
     * Can be used with the {@link CacheLoad} annotation (using parameters for the keys) to update a value in cache.
     *
     * @param ctx the invocation context.
     * @return The result from the cache load. This will either be the result returned by the
     *      {@code ctx.proceed()} function call or the value held in the cache.
     * @throws Exception Exception thrown by the caching implementation or the target method.
     */
    @AroundInvoke
    public Object loadIntoCache(final InvocationContext ctx) throws Throwable {


        final Method method = ctx.getMethod();
        final Class<?> returnedClass = method.getReturnType();
        final Object[] params = ctx.getParameters();
        final Loader<Object, Object> loader = param -> ctx.proceed();
        final Proceeder<Object> proceeder = () -> ctx.proceed();

        return LoadOnCacheMiss(returnedClass, method, proceeder, params, loader);
    }

}
