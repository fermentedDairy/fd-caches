package org.fermented.dairy.caches.interceptors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.fermented.dairy.caches.api.interfaces.Cache;
import org.fermented.dairy.caches.interceptors.annotations.CacheDelete;
import org.fermented.dairy.caches.interceptors.annotations.CacheKey;
import org.fermented.dairy.caches.interceptors.annotations.CacheLoad;
import org.fermented.dairy.caches.interceptors.annotations.Cached;
import org.fermented.dairy.caches.interceptors.exceptions.CacheInterceptorException;
import org.fermented.dairy.caches.interceptors.exceptions.CacheInterceptorRuntimeException;

/**
 * Abstract parent class for all interceptors.
 */
@Dependent
public class AbstractCacheInterceptor {

    private static final String CONFIG_TEMPLATE = "fd.config.cache.%s.%s";

    @Inject
    @ConfigProperty(name = "fd.caches.provider.default", defaultValue = "internal.default.cache")
    private String defaultProviderName;

    @Inject
    @ConfigProperty(name = "fd.caches.ttl.default", defaultValue = "3000")
    private long defaultTtl;

    @Inject
    private Config config;

    @Inject
    private Instance<Cache> providers;

    private Map<String, Cache> cacheNameMap;

    @PostConstruct
    private void init() {
        cacheNameMap = providers.stream().collect(Collectors.toMap(
                Cache::getProviderName,
                Function.identity()
        ));

        if (!cacheNameMap.containsKey(defaultProviderName)) {
            throw new CacheInterceptorRuntimeException("Could not load default cache instance");
        }
    }

    protected Cache getCacheForLoad(final Method method) {
        final Class<?> returnType = getActualReturnedClass(method);
        final Optional<String> cacheProviderConfig = config.getOptionalValue(
                CONFIG_TEMPLATE.formatted(returnType.getCanonicalName(), "cacheprovider"), String.class);
        if (cacheProviderConfig.isPresent()) {
            return cacheNameMap.getOrDefault(cacheProviderConfig.get(), cacheNameMap.get(defaultProviderName));
        }

        final Optional<Cached> optionalCachedAnnotation = getCachedAnnotation(method.getReturnType(), method);
        final String cacheName = optionalCachedAnnotation
                .map(Cached::cacheProviderName)
                .filter(str -> !str.trim().isEmpty())
                .map(String::trim)
                .orElse(defaultProviderName);

        return cacheNameMap.getOrDefault(cacheName, cacheNameMap.get(defaultProviderName));
    }

    protected Cache getCacheForDelete(final Method method) throws CacheInterceptorException {
        final Class<?> cacheClass = getCachedClassForDelete(method);

        final Optional<String> cacheProviderConfig = config.getOptionalValue(
                CONFIG_TEMPLATE.formatted(cacheClass.getCanonicalName(), "cacheprovider"), String.class);
        if (cacheProviderConfig.isPresent()) {
            return cacheNameMap.getOrDefault(cacheProviderConfig.get(), cacheNameMap.get(defaultProviderName));
        }

        final Optional<Cached> optionalCachedAnnotation = getCachedAnnotation(cacheClass, method);
        final String cacheName = optionalCachedAnnotation
                .map(Cached::cacheProviderName)
                .filter(str -> !str.trim().isEmpty())
                .map(String::trim)
                .orElse(defaultProviderName);

        return cacheNameMap.getOrDefault(cacheName, cacheNameMap.get(defaultProviderName));
    }

    private static Class<?> getCachedClassForDelete(final Method method) throws CacheInterceptorException {
        final Class<?> cacheClass = Objects.requireNonNullElseGet(
                method.getAnnotation(CacheDelete.class),
                () -> {
                    throw new CacheInterceptorRuntimeException("CacheDelete annotation must be present"); //hack for throw if null
                }).cacheClass();
        if (cacheClass.equals(Void.class)) {
            return cachedBeanFromArguments(method);
        }
        return cacheClass;
    }

    private static Class<?> cachedBeanFromArguments(final Method method) throws CacheInterceptorException {

        return Arrays.stream(method.getParameters())
                .map(Parameter::getType)
                .filter(klass -> klass.isAnnotationPresent(Cached.class))
                .findFirst().orElseThrow(() -> new CacheInterceptorException("Could not determine cached class for method %s", method.getName()));
    }

    protected Class<?> getActualReturnedClass(final Method method) {
        if (method.getReturnType().isAssignableFrom(Optional.class)) {
            return method.getAnnotation(CacheLoad.class).optionalClass();
        } else {
            return method.getReturnType();
        }
    }

    protected long getTtl(final Method method) {

        final Class<?> returnType = getActualReturnedClass(method);
        final Optional<Long> ttlConfig = config.getOptionalValue(
                CONFIG_TEMPLATE.formatted(returnType.getCanonicalName(), "ttlms"), Long.class);
        if (ttlConfig.isPresent()) {
            return ttlConfig.get();
        }

        final Optional<Cached> optionalCachedAnnotation = getCachedAnnotation(method.getReturnType(), method);
        return optionalCachedAnnotation
                .map(Cached::ttlMilliSeconds).filter(ttlMS -> !Long.valueOf(Cached.DEFAULT_TTL).equals(ttlMS))
                .orElse(defaultTtl);
    }

    protected String getCacheName(final Method method) {

        final Class<?> returnType = getActualReturnedClass(method);
        final Optional<String> cacheNameConfig = config.getOptionalValue(
                CONFIG_TEMPLATE.formatted(returnType.getCanonicalName(), "cachename"), String.class);
        if (cacheNameConfig.isPresent()) {
            return cacheNameConfig.get();
        }

        final Optional<Cached> optionalCachedAnnotation = getCachedAnnotation(returnType, method);
        return optionalCachedAnnotation
                .map(Cached::cacheName)
                .filter(str -> !str.trim().isEmpty())
                .map(String::trim)
                .orElse(returnType.getCanonicalName());
    }

    protected String getCacheNameForDelete(final Method method) throws CacheInterceptorException {

        final Class<?> cachedClass = getCachedClassForDelete(method);
        final Optional<String> cacheNameConfig = config.getOptionalValue(
                CONFIG_TEMPLATE.formatted(cachedClass.getCanonicalName(), "cachename"), String.class);
        if (cacheNameConfig.isPresent()) {
            return cacheNameConfig.get();
        }

        final Optional<Cached> optionalCachedAnnotation = getCachedAnnotation(cachedClass, method);
        return optionalCachedAnnotation
                .map(Cached::cacheName)
                .filter(str -> !str.trim().isEmpty())
                .map(String::trim)
                .orElse(cachedClass.getCanonicalName());
    }

    private static Optional<Cached> getCachedAnnotation(final Class<?> returnType, final Method method) {
        final Optional<Cached> optionalCachedAnnotation;
        if (returnType.isAssignableFrom(Optional.class)) {
            final CacheLoad cacheLoadAnnotation = method.getAnnotation(CacheLoad.class);
            if (cacheLoadAnnotation == null) {
                throw new CacheInterceptorRuntimeException("CacheLoad annotation must be present on the intercepted method");
            }
            optionalCachedAnnotation = Optional.ofNullable(
                    cacheLoadAnnotation.optionalClass().getAnnotation(Cached.class)
            );
        } else {
            optionalCachedAnnotation = Optional.ofNullable(
                    returnType.getAnnotation(Cached.class)
            );
        }
        return optionalCachedAnnotation;
    }

    protected Object getCacheKey(final Method method, final Object[] params) throws CacheInterceptorException {

        if (params == null || params.length == 0) {
            throw new CacheInterceptorException(
                    "No parameters on method %s in class %s, could not determine cache key.",
                    method.getName(),
                    method.getDeclaringClass().getCanonicalName());
        }

        if (params.length == 1) {
            return params[0];
        }

        final Parameter[] parameters = method.getParameters();
        for (int paramIndex = 0; paramIndex < parameters.length; paramIndex++) {
            if (parameters[paramIndex].isAnnotationPresent(CacheKey.class) || parameters[paramIndex].getType().isAnnotationPresent(Cached.class)) {
                return params[paramIndex];
            }
        }
        throw new CacheInterceptorException(
                "No parameter is on annotated with the 'CacheKey' annotation or is a cached bean for method %s in class,"
                + " could not determine cache key.",
                method.getName());
    }

    protected boolean isCacheDisabled(final Method method) {
        return config.getOptionalValue(CONFIG_TEMPLATE.formatted(
                getActualReturnedClass(method).getCanonicalName(),
                "disabled"), Boolean.class).orElse(false);
    }
}
