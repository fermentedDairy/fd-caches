package org.fermented.dairy.caches.interceptors;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.fermented.dairy.caches.api.interfaces.Cache;
import org.fermented.dairy.caches.interceptors.annotations.CacheDelete;
import org.fermented.dairy.caches.interceptors.annotations.CacheKey;
import org.fermented.dairy.caches.interceptors.annotations.CacheLoad;
import org.fermented.dairy.caches.interceptors.annotations.Cached;
import org.fermented.dairy.caches.interceptors.exceptions.CacheInterceptorException;


/**
 * Interceptor for deleting caches. Caches are deleted BEFORE the intercepted method is called.
 * Can be used with the {@link CacheLoad} annotation (using parameters for the keys) to update a value in cache.
 */
@Interceptor
@CacheDelete
@Dependent
@Priority(Integer.MAX_VALUE - 1)
public class CacheDeleteInterceptor extends AbstractCacheInterceptor {

    /**
     * Interceptor method invoked around the target method (annotated with {@link CacheDelete}).
     *
     * @param ctx The InvocationContext of the intercepted method
     *
     * @return the result of {@code ctx.proceed()}
     *
     * @throws Exception The exception thrown by the {@code ctx.proceed()} call
     */
    @AroundInvoke
    public Object deleteFromCache(final InvocationContext ctx) throws Exception {

        final Cache cacheProvider = getCacheForDelete(ctx.getMethod());
        final String cacheName = getCacheNameForDelete(ctx.getMethod());
        Object key = getCacheKey(ctx.getMethod(), ctx.getParameters());
        if (key.getClass().isAnnotationPresent(Cached.class)) {
            key = getKeyFromCachedClass(key);
        }

        cacheProvider.removeValue(cacheName, key);
        return ctx.proceed();
    }

    private Object getKeyFromCachedClass(final Object key) throws CacheInterceptorException {
        final Class<?> keyClass = key.getClass();
        final Field annotatedField = Arrays.stream(keyClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CacheKey.class))
                .findFirst()
                .orElseThrow(() -> new CacheInterceptorException("No field is annotated as the CacheKey"));

        final String getterMethodName = keyClass.isRecord()
                ? annotatedField.getName()
                : "get%s".formatted(StringUtils.capitalize(annotatedField.getName()));

        final Optional<Method> getterOptional = Arrays.stream(keyClass.getMethods())
                .filter(method -> method.getName().equals(getterMethodName))
                .filter(method -> method.getParameters().length == 0)
                .findFirst();


        try {
            if (getterOptional.isPresent()) {
                return getterOptional.get().invoke(key);
            }
            annotatedField.setAccessible(true); //NOSONAR: java:S3011 - I committed to this at least once with the annotation route
            return annotatedField.get(key);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new CacheInterceptorException(e);
        }
    }
}
