package org.fermented.dairy.caches.aspects;

import java.lang.reflect.Method;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.fermented.dairy.caches.handlers.AbstractCacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CacheDeleteAspect extends AbstractCacheHandler {

    /**
     * DI friendly constructor.
     *
     * @param environment the {@link Environment Environment} to use when querying key-value pairs
     * @param cacheProviders all {@link CacheProvider CacheProviders} to use
     */
    @Autowired
    public CacheDeleteAspect(final Environment environment,
                             final List<CacheProvider> cacheProviders) {
        super(SpringConfig.using(environment), cacheProviders);
    }

    @Around("@annotation(org.fermented.dairy.caches.annotations.CacheDelete)")
    public Object deleteFromCache(final ProceedingJoinPoint jp) throws Throwable {
        final MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        final Method method = methodSignature.getMethod();
        final Object[] params = jp.getArgs();

        deleteFromCache(method, params);

        return jp.proceed();
    }

}
