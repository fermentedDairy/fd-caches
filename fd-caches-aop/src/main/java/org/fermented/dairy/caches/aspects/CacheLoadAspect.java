package org.fermented.dairy.caches.aspects;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.fermented.dairy.caches.api.exceptions.CacheException;
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

        if (returnedClass.isAssignableFrom(void.class) || returnedClass.isAssignableFrom(Void.class)) {
            throw new CacheException("void types cannot be cached");
        }
        if (isCacheDisabled(method)) {
            return jp.proceed();
        }

        final Object cacheKey = getCacheKey(method, params);

        if (returnedClass.isAssignableFrom(Optional.class)) {
            final Class<?> returnOptionalClass = getActualReturnedClass(method);
            return getCacheForLoad(method).loadOptional(cacheKey,
                    param -> (Optional) jp.proceed(),
                    getCacheName(method),
                    getTtl(method),
                    cacheKey.getClass(),
                    returnOptionalClass);
        } else {
            return getCacheForLoad(method).load(cacheKey,
                    param -> jp.proceed(),
                    getCacheName(method),
                    getTtl(method),
                    cacheKey.getClass(),
                    returnedClass);
        }

    }
}
