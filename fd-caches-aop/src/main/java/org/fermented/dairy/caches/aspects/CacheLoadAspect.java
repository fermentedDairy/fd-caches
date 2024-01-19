package org.fermented.dairy.caches.aspects;

import java.lang.reflect.Method;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.functions.Proceeder;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.fermented.dairy.caches.handlers.AbstractCacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Aspect
public class CacheLoadAspect extends AbstractCacheHandler {

    /**
     * DI friendly Constructor.
     *
     * @param environment the {@link Environment Environment} to use when querying key-value pairs
     * @param cacheProviders all {@link CacheProvider CacheProviders} to use
     */
    @Autowired
    public CacheLoadAspect(final Environment environment,
                           final List<CacheProvider> cacheProviders) {
        super(SpringConfig.using(environment), cacheProviders);
    }

    @Around("@annotation(org.fermented.dairy.caches.annotations.CacheLoad)")
    public Object loadIntoCache(final ProceedingJoinPoint jp) throws Throwable {

        final MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        final Method method = methodSignature.getMethod();
        final Class<?> returnedClass = method.getReturnType();
        final Object[] params = jp.getArgs();
        final Loader<Object, Object> loader = param -> jp.proceed();
        final Proceeder<Object> proceeder = jp::proceed;

        return loadOnCacheMiss(returnedClass, method, proceeder, params, loader);

    }
}
