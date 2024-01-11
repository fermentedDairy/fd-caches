package org.fermented.dairy.caches.aspects.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.fermented.dairy.caches.interceptors.entities.DefaultCacheEntityClass;

import java.lang.reflect.Method;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

public final class AspectUtils {
    private AspectUtils(){};

    public static ProceedingJoinPoint getProceedingJoinPoint(final Method interceptedMethod,
                                                             final Object proceedResult,
                                                             final Object... params) throws Throwable {
        final ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        final MethodSignature signature = mock(MethodSignature.class);

        lenient().when(pjp.getSignature()).thenReturn(signature);

        lenient().when(signature.getMethod()).thenReturn(interceptedMethod);

        lenient().when(pjp.getArgs()).thenReturn(params);
        lenient().when(pjp.proceed()).thenAnswer(invocationOnMock -> proceedResult);
        return pjp;
    }


}
