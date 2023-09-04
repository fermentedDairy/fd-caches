package org.fermented.dairy.caches.interceptors;

import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;
import org.fermented.dairy.caches.interceptors.annotations.CacheLoad;
import org.fermented.dairy.caches.interceptors.exceptions.CacheInterceptorException;

/**
 * CDI caching interceptor.
 */
@Interceptor
@CacheLoad
@Dependent
public class CacheLoadInterceptor extends AbstractCacheInterceptor {

    /**
     * Interceptor method invoked around the target method (annotated with {@link CacheLoad}).
     * Can be used with the {@link CacheLoad} annotation (using parameters for the keys) to update a value in cache.
     *
     * @param ctx the invocation context.
     *
     * @return The result from the cache load. This will either be the result returned by the
     *      {@code ctx.proceed()} function call or the value held in the cache.
     *
     * @throws Exception Exception thrown by the caching implementation or the target method.
     */
    @AroundInvoke
    public Object loadIntoCache(final InvocationContext ctx) throws Exception {


        final Method method = ctx.getMethod();
        final Class<?> returnedClass = method.getReturnType();

        if(returnedClass.isAssignableFrom(void.class) || returnedClass.isAssignableFrom(Void.class)) {
            throw new CacheInterceptorException("void types cannot be cached");
        }
        if(isCacheDisabled(method)) {
            return ctx.proceed();
        }

        final Object[] params = ctx.getParameters();
        final Object cacheKey = getCacheKey(method, params);

        if (returnedClass.isAssignableFrom(Optional.class)) {
            final Class<?> returnOptionalClass = getActualReturnedClass(method);
            return getCache(method).loadOptional(cacheKey,
                    param -> (Optional) ctx.proceed(),
                    getCacheName(method),
                    getTtl(method),
                    cacheKey.getClass(),
                    returnOptionalClass);
        } else {
            return getCache(method).load(cacheKey,
                    param -> ctx.proceed(),
                    getCacheName(method),
                    getTtl(method),
                    cacheKey.getClass(),
                    returnedClass);
        }
    }

}
