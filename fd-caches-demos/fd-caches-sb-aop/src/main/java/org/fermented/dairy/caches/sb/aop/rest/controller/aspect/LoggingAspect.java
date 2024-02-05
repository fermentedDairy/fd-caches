package org.fermented.dairy.caches.sb.aop.rest.controller.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(org.fermented.dairy.caches.sb.aop.rest.controller.aspect.Logged)")
    public Object aroundLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        System.out.printf("Around %s.%s%n", methodSignature.getMethod().getDeclaringClass().getCanonicalName(),
                methodSignature.getMethod().getName());
        return proceedingJoinPoint.proceed();
    }
}
