package org.fermented.dairy.caches.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class CacheLoadAspect {

    @Pointcut("@annotation(org.fermented.dairy.caches.annotations.CacheLoad)")
    public void loadingMethods() {}

    @Around("loadingMethods()")
    public Object loadIntoCache(final ProceedingJoinPoint jp) throws Throwable {

        return jp.proceed();

    }
}
