package org.fermented.dairy.caches.interceptors.utils;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;

public final class ContextUtils {
    private ContextUtils(){}


    public static InvocationContext getInvocationContext(final Method interceptedMethod, final Object proceedResult, final Object... params) throws Exception {
        final InvocationContext context = mock(InvocationContext.class);
        lenient().when(context.getMethod()).thenReturn(interceptedMethod);
        lenient().when(context.getParameters()).thenReturn(params);
        lenient().when(context.proceed()).thenAnswer(invocationOnMock -> proceedResult);
        return context;
    }
}
