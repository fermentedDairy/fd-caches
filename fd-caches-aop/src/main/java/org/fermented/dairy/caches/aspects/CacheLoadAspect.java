package org.fermented.dairy.caches.aspects;

import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

        return jp.proceed();

    }
}
