package org.fermented.dairy.caches.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.fermented.dairy.caches.handlers.AbstractCacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.List;

@Aspect
public class CacheLoadAspect extends AbstractCacheHandler {

    /**
     * DI friendly Constructor.
     *
     * @param environment the {@link Environment Environment} to use when querying key-value pairs
     * @param cacheProviders all {@link CacheProvider CacheProviders} to use
     */
    public CacheLoadAspect(@Autowired final Environment environment,
                           @Autowired final List<CacheProvider> cacheProviders) {
        super(SpringConfig.using(environment), cacheProviders);
    }

    @Pointcut("@annotation(org.fermented.dairy.caches.annotations.CacheLoad)")
    public void loadingMethods() {}

    @Around("loadingMethods()")
    public Object loadIntoCache(final ProceedingJoinPoint jp) throws Throwable {

        return jp.proceed();

    }
}
