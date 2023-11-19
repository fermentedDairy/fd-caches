package org.fermented.dairy.caches.interceptors;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.Config;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.fermented.dairy.caches.interceptors.annotations.CacheKey;
import org.fermented.dairy.caches.interceptors.annotations.Cached;
import org.fermented.dairy.caches.interceptors.annotations.CachedType;
import org.fermented.dairy.caches.interceptors.exceptions.CacheInterceptorException;
import org.fermented.dairy.caches.interceptors.exceptions.CacheInterceptorRuntimeException;


/**
 * Abstract parent class for all interceptors.
 */
@Dependent
public class AbstractCacheInterceptor {

    private static final String CONFIG_TEMPLATE = "fd.config.cache.%s.%s";

    private static final long ONE_HOUR_IN_MILLIS = 60L * 60L * 1000L; //60 minutes * 60 seconds * 1000

    private final String defaultProviderName;

    private final long defaultTtl;

    private final Config config;

    private final Instance<CacheProvider> providers;

    private Map<String, CacheProvider> cacheNameMap;

    @Inject
    public AbstractCacheInterceptor(
            final Config config,
            final Instance<CacheProvider> providers) {
        this.defaultProviderName = config
                .getOptionalValue("fd.config.cache.provider.default", String.class)
                .orElse("internal.default.cache");
        this.defaultTtl = config
                .getOptionalValue("fd.config.cache.ttl.default", Long.class)
                .orElse(ONE_HOUR_IN_MILLIS);
        this.config = config;
        this.providers = providers;
        initCacheNameMap();
    }

    protected static Class<?> getCachedClassForDelete(final Method method) throws CacheInterceptorException {

        final CachedType cachedTypeAnnotation = method.getAnnotation(CachedType.class);
        if (cachedTypeAnnotation != null) {
            return cachedTypeAnnotation.value();
        } else {
            return cachedBeanFromArguments(method);
        }
    }

    protected static Class<?> cachedBeanFromArguments(final Method method) throws CacheInterceptorException {

        return Arrays.stream(method.getParameters())
                .map(Parameter::getType)
                .filter(klass -> klass.isAnnotationPresent(Cached.class))
                .findFirst().orElseThrow(() -> new CacheInterceptorException("Could not determine cached class for method %s", method.getName()));
    }

    protected CacheProvider getCacheForLoad(final Method method) {
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

    protected CacheProvider getCacheForDelete(final Method method) throws CacheInterceptorException {
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

    protected Class<?> getActualReturnedClass(final Method method) {
        if (method.getReturnType().isAssignableFrom(Optional.class)) {
            CachedType cachedTypeAnnotation;
            if ((cachedTypeAnnotation = method.getAnnotation(CachedType.class)) == null) {
                throw new CacheInterceptorRuntimeException("%s returns an optional and must be annotated with 'CachedType'".formatted(method.getName()));
            }
            return cachedTypeAnnotation.value();
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
        return getCacheNameFromConfig(method, returnType);
    }

    protected String getCacheNameFromConfig(final Method method, final Class<?> cachedClass) {
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

    protected String getCacheNameForDelete(final Method method) throws CacheInterceptorException {
        final Class<?> cachedClass = getCachedClassForDelete(method);
        return getCacheNameFromConfig(method, cachedClass);
    }

    private void initCacheNameMap() {
        cacheNameMap = providers.stream().collect(Collectors.toMap(
                CacheProvider::getProviderName,
                Function.identity()
        ));

        if (!cacheNameMap.containsKey(defaultProviderName)) {
            throw new CacheInterceptorRuntimeException("Could not load default cache instance");
        }
    }

    private static Optional<Cached> getCachedAnnotation(final Class<?> returnType, final Method method) {
        final Optional<Cached> optionalCachedAnnotation;
        if (returnType.isAssignableFrom(Optional.class)) {
            final CachedType cachedTypeAnnotation = method.getAnnotation(CachedType.class);
            if (cachedTypeAnnotation == null) {
                throw new CacheInterceptorRuntimeException("CachedType annotation must be present when returning Optionals");
            }
            optionalCachedAnnotation = Optional.ofNullable(
                    cachedTypeAnnotation.value().getAnnotation(Cached.class)
            );
        } else {
            optionalCachedAnnotation = Optional.ofNullable(
                    returnType.getAnnotation(Cached.class)
            );
        }
        return optionalCachedAnnotation;
    }
}
