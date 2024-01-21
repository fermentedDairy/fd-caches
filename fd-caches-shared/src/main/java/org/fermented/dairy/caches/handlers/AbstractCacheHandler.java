package org.fermented.dairy.caches.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;
import org.fermented.dairy.caches.annotations.CachedType;
import org.fermented.dairy.caches.api.exceptions.CacheException;
import org.fermented.dairy.caches.api.exceptions.CacheRuntimeException;
import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.functions.Proceeder;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;


/**
 * Abstract parent class for all interceptors.
 */
public class AbstractCacheHandler {

    private static final String CONFIG_TEMPLATE = "fd.config.cache.%s.%s";

    private static final long ONE_HOUR_IN_MILLIS = 60L * 60L * 1000L; //60 minutes * 60 seconds * 1000

    private final String defaultProviderName;

    private final long defaultTtl;

    private final CacheConfig config;

    private final Map<String, CacheProvider> cacheNameMap;

    /**
     * Constructor with injection points.
     *
     * @param config the Config
     * @param caches all CacheProvider implementations to use for caching
     */
    public AbstractCacheHandler(
            final CacheConfig config,
            final Iterable<CacheProvider> caches) {
        this.defaultProviderName = config
                .getOptionalValue("fd.config.cache.provider.default", String.class)
                .orElse("internal.default.cache");
        this.defaultTtl = config
                .getOptionalValue("fd.config.cache.ttl.default", Long.class)
                .orElse(ONE_HOUR_IN_MILLIS);
        this.config = config;

        this.cacheNameMap = initCacheNameMap(caches);
        if (cacheNameMap.size() == 1) {
            cacheNameMap.put(defaultProviderName, cacheNameMap.values().iterator().next());
        }
        if (!cacheNameMap.containsKey(defaultProviderName)) {
            throw new CacheRuntimeException("Could not load default cache instance");
        }
    }

    protected Object getFromCacheOrLoad(final Class<?> returnedClass,
                                        final Method method,
                                        final Proceeder<Object> proceeder,
                                        final Object[] params,
                                        final Loader<Object, Object> loader) throws Throwable {
        if (returnedClass.isAssignableFrom(void.class) || returnedClass.isAssignableFrom(Void.class)) {
            throw new CacheException("void types cannot be cached");
        }
        if (isCacheDisabled(method)) {
            return proceeder.proceed();
        }
        final Object cacheKey = getCacheKey(method, params);

        if (returnedClass.isAssignableFrom(Optional.class)) {
            final Class<?> returnOptionalClass = getActualReturnedClass(method);
            return getCacheForLoad(method).loadOptional(cacheKey,
                    param -> (Optional) loader.load(param),
                    getCacheName(method),
                    getTtl(method),
                    cacheKey.getClass(),
                    returnOptionalClass);
        } else {
            return getCacheForLoad(method).load(cacheKey,
                    loader,
                    getCacheName(method),
                    getTtl(method),
                    cacheKey.getClass(),
                    returnedClass);
        }
    }

    protected void deleteFromCache(final Method method, final Object[] params) throws CacheException {
        final CacheProvider cacheProvider = getCacheForDelete(method);
        final String cacheName = getCacheNameForDelete(method);
        Object key = getCacheKey(method, params);
        if (key.getClass().isAnnotationPresent(Cached.class)) {
            key = getKeyFromCachedClass(key);
        }

        cacheProvider.removeValue(cacheName, key);
    }

    private static Class<?> getCachedClassForDelete(final Method method) throws CacheException {

        final CachedType cachedTypeAnnotation = method.getAnnotation(CachedType.class);
        if (cachedTypeAnnotation != null) {
            return cachedTypeAnnotation.value();
        } else {
            return cachedBeanFromArguments(method);
        }
    }

    private static Class<?> cachedBeanFromArguments(final Method method) throws CacheException {

        return Arrays.stream(method.getParameters())
                .map(Parameter::getType)
                .filter(klass -> klass.isAnnotationPresent(Cached.class))
                .findFirst().orElseThrow(() -> new CacheException("Could not determine cached class for method %s", method.getName()));
    }

    private CacheProvider getCacheForLoad(final Method method) {
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

    private CacheProvider getCacheForDelete(final Method method) throws CacheException {
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

    private Class<?> getActualReturnedClass(final Method method) {
        if (method.getReturnType().isAssignableFrom(Optional.class)) {
            final CachedType cachedTypeAnnotation;
            if ((cachedTypeAnnotation = method.getAnnotation(CachedType.class)) == null) {
                throw new CacheRuntimeException(
                        "%s returns an optional and must be annotated with 'CachedType'".formatted(method.getName()));
            }
            return cachedTypeAnnotation.value();
        } else {
            return method.getReturnType();
        }
    }

    private long getTtl(final Method method) {

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

    private String getCacheName(final Method method) {

        final Class<?> returnType = getActualReturnedClass(method);
        return getCacheNameFromConfig(method, returnType);
    }

    private String getCacheNameFromConfig(final Method method, final Class<?> cachedClass) {
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

    private Object getCacheKey(final Method method, final Object[] params) throws CacheException {

        if (params == null || params.length == 0) {
            throw new CacheException(
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
        throw new CacheException(
                "No parameter is on annotated with the 'CacheKey' annotation or is a cached bean for method %s in class,"
                        + " could not determine cache key.",
                method.getName());
    }

    private boolean isCacheDisabled(final Method method) {
        return config.getOptionalValue(CONFIG_TEMPLATE.formatted(
                getActualReturnedClass(method).getCanonicalName(),
                "disabled"), Boolean.class).orElse(false);
    }

    private String getCacheNameForDelete(final Method method) throws CacheException {
        final Class<?> cachedClass = getCachedClassForDelete(method);
        return getCacheNameFromConfig(method, cachedClass);
    }

    private static Optional<Cached> getCachedAnnotation(final Class<?> returnType, final Method method) {
        final Optional<Cached> optionalCachedAnnotation;
        if (returnType.isAssignableFrom(Optional.class)) {
            final CachedType cachedTypeAnnotation = method.getAnnotation(CachedType.class);
            if (cachedTypeAnnotation == null) {
                throw new CacheRuntimeException("CachedType annotation must be present when returning Optionals");
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

    private Object getKeyFromCachedClass(final Object key) throws CacheException {
        final Class<?> keyClass = key.getClass();
        final Field annotatedField = Arrays.stream(keyClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CacheKey.class))
                .findFirst()
                .orElseThrow(() -> new CacheException("No field is annotated as the CacheKey"));

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
            throw new CacheException(e);
        }
    }

    /**
     * Constructs a map of cache names to {@link CacheProvider CacheProviders}.
     *
     * @param providers Instance containing provider instances.
     *
     * @return map of cache names to {@link CacheProvider CacheProviders}.
     */
    private static Map<String, CacheProvider> initCacheNameMap(final Iterable<CacheProvider> providers) {
        return  StreamSupport.stream(providers.spliterator(), false).collect(Collectors.toMap(
                CacheProvider::getProviderName,
                Function.identity()
        ));
    }

}
