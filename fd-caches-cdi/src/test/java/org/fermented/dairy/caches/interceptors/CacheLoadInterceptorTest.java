package org.fermented.dairy.caches.interceptors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.enterprise.inject.Instance;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.functions.OptionalLoader;
import org.fermented.dairy.caches.api.interfaces.Cache;
import org.fermented.dairy.caches.interceptors.beans.CacheBean;
import org.fermented.dairy.caches.interceptors.entities.DefaultCacheEntityClass;
import org.fermented.dairy.caches.interceptors.entities.NamedCachedBean;
import org.fermented.dairy.caches.interceptors.exceptions.CacheInterceptorException;
import org.fermented.dairy.caches.interceptors.utils.ContextUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CacheLoadInterceptorTest {

    CacheLoadInterceptor cacheLoadInterceptor;

    @Mock
    Instance<Cache> providers;

    @Mock
    Cache defaultCache;

    @Mock
    Cache cache1;

    @Mock
    Cache cache2;

    @Mock
    Config config;

    List<Cache> cacheInstances;

    @BeforeEach
    void init() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        initInjectedCacheInstances();
    }

    void initInjectedCacheInstances() {
        cacheInstances = List.of(
                cache1, cache2, defaultCache
        );
        lenient().when(defaultCache.getProviderName()).thenReturn("default");
        lenient().when(cache1.getProviderName()).thenReturn("cache1");
        lenient().when(cache2.getProviderName()).thenReturn("cache2");
        lenient().when(providers.iterator()).thenReturn(cacheInstances.iterator());
        lenient().when(providers.stream()).thenReturn(cacheInstances.stream());
        lenient().when(config.getOptionalValue("fd.caches.ttl.default", String.class))
                .thenReturn(Optional.of("default"));
        lenient().when(config.getOptionalValue("fd.caches.provider.default", Long.class))
                .thenReturn(Optional.of(3000L));
        cacheLoadInterceptor = new CacheLoadInterceptor(config, providers);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param. Default cache is used.
             Method: DefaultCacheEntity load(Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheCorrectKeyType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultLoad", Long.class);
        final Long key = 1L;
        when(defaultCache.load(
                any(Object.class),
                any(Loader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        )).thenAnswer(invocationOnMock -> {
            assertAll("Validate Parameters",
                    () -> assertEquals(key, invocationOnMock.getArgument(0), "Key is incorrect"),
                    () -> assertEquals(DefaultCacheEntityClass.class.getCanonicalName(), invocationOnMock.getArgument(2), "Cache name is incorrect"),
                    () -> assertEquals(3000L, (long)invocationOnMock.getArgument(3), "ttl is incorrect"),
                    () -> assertEquals(Long.class, invocationOnMock.getArgument(4), "keyClass is incorrect"),
                    () -> assertEquals(DefaultCacheEntityClass.class, invocationOnMock.getArgument(5), "valueClass is incorrect"));
            return ((Loader)invocationOnMock.getArgument(1)).load(invocationOnMock.getArgument(0));
        });
        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), key));
        assertEquals(new DefaultCacheEntityClass(1L), actual);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param. Cache and cache name from class is used.
             Method: NamedCacheBean namedLoad(Long param)
            """)
    @Test
    void singleUnannotatedOverriddenCacheCorrectKeyType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("namedLoad", Long.class);
        final Long key = 1L;
        when(cache1.load(
                any(Object.class),
                any(Loader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        )).thenAnswer(invocationOnMock -> {
            assertAll("Validate Parameters",
                    () -> assertEquals(key, invocationOnMock.getArgument(0), "Key is incorrect"),
                    () -> assertEquals("overriddenCacheName", invocationOnMock.getArgument(2), "Cache name is incorrect"),
                    () -> assertEquals(10L, (long)invocationOnMock.getArgument(3), "ttl is incorrect"),
                    () -> assertEquals(Long.class, invocationOnMock.getArgument(4), "keyClass is incorrect"),
                    () -> assertEquals(NamedCachedBean.class, invocationOnMock.getArgument(5), "valueClass is incorrect"));
            return ((Loader)invocationOnMock.getArgument(1)).load(invocationOnMock.getArgument(0));
        });
        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), key));
        assertEquals(new DefaultCacheEntityClass(1L), actual);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param. Cache, cache name and TTL overridden from config.
             Method: NamedCacheBean namedLoad(Long param)
            """)
    @Test
    void singleUnannotatedOverriddenCacheCorrectKeyTypeConfigOverrides() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("namedLoad", Long.class);
        final Long key = 1L;

        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".disabled", Boolean.class)).thenReturn(Optional.empty());
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cacheprovider", String.class)).thenReturn(Optional.of("cache2"));
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cachename", String.class)).thenReturn(Optional.of("configcachename"));
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".ttlms", Long.class)).thenReturn(Optional.of(1234L));

        when(cache2.load(
                any(Object.class),
                any(Loader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        )).thenAnswer(invocationOnMock -> {
            assertAll("Validate Parameters",
                    () -> assertEquals(key, invocationOnMock.getArgument(0), "Key is incorrect"),
                    () -> assertEquals("configcachename", invocationOnMock.getArgument(2), "Cache name is incorrect"),
                    () -> assertEquals(1234L, (long)invocationOnMock.getArgument(3), "ttl is incorrect"),
                    () -> assertEquals(Long.class, invocationOnMock.getArgument(4), "keyClass is incorrect"),
                    () -> assertEquals(NamedCachedBean.class, invocationOnMock.getArgument(5), "valueClass is incorrect"));
            return ((Loader)invocationOnMock.getArgument(1)).load(invocationOnMock.getArgument(0));
        });
        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), key));
        assertEquals(new DefaultCacheEntityClass(1L), actual);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param. Cache disabled in config
             Method: NamedCacheBean namedLoad(Long param)
            """)
    @Test
    void singleUnannotatedOverriddenCacheCorrectKeyTypeConfigCacheDisabled() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("namedLoad", Long.class);
        final Long key = 1L;

        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".disabled", Boolean.class)).thenReturn(Optional.of(true));

        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), key));
        verify(cache1, never()).load(
                any(Object.class),
                any(Loader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        );
        verify(cache2, never()).load(
                any(Object.class),
                any(Loader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        );
        verify(defaultCache, never()).load(
                any(Object.class),
                any(Loader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        );
        assertEquals(new DefaultCacheEntityClass(1L), actual);
    }

    @DisplayName("""
            The intercepted method has multiple params, one is annotated as a key. Param is the same type as annotated key member in DefaultCacheEntity, default cache is used.
             Method: DefaultCacheEntity defaultLoad(Object dummy, @CacheKey Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheCorrectAnnotatedKeyInParamsType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultLoad", Object.class, Long.class);
        final Long key = 1L;

        when(defaultCache.load(
                any(Object.class),
                any(Loader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        )).thenAnswer(invocationOnMock -> {
            assertAll("Validate Parameters",
                    () -> assertEquals(key, invocationOnMock.getArgument(0), "Key is incorrect"),
                    () -> assertEquals(DefaultCacheEntityClass.class.getCanonicalName(), invocationOnMock.getArgument(2), "Cache name is incorrect"),
                    () -> assertEquals(3000L, (long)invocationOnMock.getArgument(3), "ttl is incorrect"),
                    () -> assertEquals(Long.class, invocationOnMock.getArgument(4), "keyClass is incorrect"),
                    () -> assertEquals(DefaultCacheEntityClass.class, invocationOnMock.getArgument(5), "valueClass is incorrect"));
            return ((Loader)invocationOnMock.getArgument(1)).load(invocationOnMock.getArgument(0));
        });
        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), "string", key));
        assertEquals(new DefaultCacheEntityClass(1L), actual);
    }

    @DisplayName("""
            The intercepted method has multiple params, none annotated as a key, exception is thrown.
             Method: DefaultCacheEntity defaultLoad(Long dummy, Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheNoAnnotatedKeyInParamsType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultLoad", Long.class, Long.class);
        final Long key = 1L;

        final CacheInterceptorException actualException = assertThrows(CacheInterceptorException.class, () -> cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), 1234L, key)));
        assertEquals("No parameter is on annotated with the 'CacheKey' annotation or is a cached bean for method defaultLoad in class, could not determine cache key.",
                actualException.getMessage());
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Param is the same type as annotated key member in DefaultCacheEntity, default cache is used.
             Method: Optional<DefaultCacheEntity> load(Long param)
            """)
    @Test
    void singleUnannotatedOptionalDefaultCacheCorrectKeyType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultOptionalLoad", Long.class);
        final Long key = 1L;

        when(defaultCache.loadOptional(
                any(Object.class),
                any(OptionalLoader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        )).thenAnswer(invocationOnMock -> {
            assertAll("Validate Parameters",
                    () -> assertEquals(key, invocationOnMock.getArgument(0), "Key is incorrect"),
                    () -> assertEquals(DefaultCacheEntityClass.class.getCanonicalName(), invocationOnMock.getArgument(2), "Cache name is incorrect"),
                    () -> assertEquals(3000L, (long)invocationOnMock.getArgument(3), "ttl is incorrect"),
                    () -> assertEquals(Long.class, invocationOnMock.getArgument(4), "keyClass is incorrect"),
                    () -> assertEquals(DefaultCacheEntityClass.class, invocationOnMock.getArgument(5), "valueClass is incorrect"));
            return ((OptionalLoader)invocationOnMock.getArgument(1)).load(invocationOnMock.getArgument(0));
        });
        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, Optional.of(new DefaultCacheEntityClass(key)), key));
        assertEquals(Optional.of(new DefaultCacheEntityClass(1L)), actual);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Cache and cache name is used from annotation.
             Method: Optional<DefaultCacheEntity> load(Long param)
            """)
    @Test
    void singleUnannotatedOptionalNamedCacheCorrectKeyType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("namedOptionalLoad", Long.class);
        final Long key = 1L;
        when(cache1.loadOptional(
                any(Object.class),
                any(OptionalLoader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        )).thenAnswer(invocationOnMock -> {
            assertAll("Validate Parameters",
                    () -> assertEquals(key, invocationOnMock.getArgument(0), "Key is incorrect"),
                    () -> assertEquals("overriddenCacheName", invocationOnMock.getArgument(2), "Cache name is incorrect"),
                    () -> assertEquals(10L, (long)invocationOnMock.getArgument(3), "ttl is incorrect"),
                    () -> assertEquals(Long.class, invocationOnMock.getArgument(4), "keyClass is incorrect"),
                    () -> assertEquals(NamedCachedBean.class, invocationOnMock.getArgument(5), "valueClass is incorrect"));
            return ((OptionalLoader)invocationOnMock.getArgument(1)).load(invocationOnMock.getArgument(0));
        });
        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, Optional.of(new NamedCachedBean(key)), key));
        assertEquals(Optional.of(new NamedCachedBean(1L)), actual);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Cache disabled in config.
             Method: Optional<DefaultCacheEntity> load(Long param)
            """)
    @Test
    void singleUnannotatedOptionalNamedCacheCorrectKeyTypeConfigDisabled() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("namedOptionalLoad", Long.class);
        final Long key = 1L;

        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".disabled", Boolean.class)).thenReturn(Optional.of(true));

        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, Optional.of(new NamedCachedBean(key)), key));
        verify(cache1, never()).loadOptional(
                any(Object.class),
                any(OptionalLoader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        );
        verify(cache2, never()).loadOptional(
                any(Object.class),
                any(OptionalLoader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        );
        verify(defaultCache, never()).loadOptional(
                any(Object.class),
                any(OptionalLoader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        );
        assertEquals(Optional.of(new NamedCachedBean(1L)), actual);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Cache and cache name is used from config.
             Method: Optional<DefaultCacheEntity> load(Long param)
            """)
    @Test
    void singleUnannotatedOptionalNamedCacheCorrectKeyTypeConfigOverrides() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("namedOptionalLoad", Long.class);
        final Long key = 1L;

        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".disabled", Boolean.class)).thenReturn(Optional.empty());
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cacheprovider", String.class)).thenReturn(Optional.of("cache2"));
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cachename", String.class)).thenReturn(Optional.of("configcachename"));
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".ttlms", Long.class)).thenReturn(Optional.of(1234L));

        when(cache2.loadOptional(
                any(Object.class),
                any(OptionalLoader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        )).thenAnswer(invocationOnMock -> {
            assertAll("Validate Parameters",
                    () -> assertEquals(key, invocationOnMock.getArgument(0), "Key is incorrect"),
                    () -> assertEquals("configcachename", invocationOnMock.getArgument(2), "Cache name is incorrect"),
                    () -> assertEquals(1234L, (long)invocationOnMock.getArgument(3), "ttl is incorrect"),
                    () -> assertEquals(Long.class, invocationOnMock.getArgument(4), "keyClass is incorrect"),
                    () -> assertEquals(NamedCachedBean.class, invocationOnMock.getArgument(5), "valueClass is incorrect"));
            return ((OptionalLoader)invocationOnMock.getArgument(1)).load(invocationOnMock.getArgument(0));
        });
        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, Optional.of(new NamedCachedBean(key)), key));
        assertEquals(Optional.of(new NamedCachedBean(1L)), actual);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Param is the same type as annotated key member in DefaultCacheEntity, default cache is used.
             Method: Optional<DefaultCacheEntity> defaultOptionalLoad(Object dummy, @CacheKey Long param)
            """)
    @Test
    void singleUnannotatedOptionalDefaultCacheCorrectKeyAnnotatedType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultOptionalLoad", Object.class, Long.class);
        final Long key = 1L;
        when(defaultCache.loadOptional(
                any(Object.class),
                any(OptionalLoader.class),
                any(String.class),
                any(Long.class),
                any(Class.class),
                any(Class.class)
        )).thenAnswer(invocationOnMock -> {
            assertAll("Validate Parameters",
                    () -> assertEquals(key, invocationOnMock.getArgument(0), "Key is incorrect"),
                    () -> assertEquals(DefaultCacheEntityClass.class.getCanonicalName(), invocationOnMock.getArgument(2), "Cache name is incorrect"),
                    () -> assertEquals(3000L, (long)invocationOnMock.getArgument(3), "ttl is incorrect"),
                    () -> assertEquals(Long.class, invocationOnMock.getArgument(4), "keyClass is incorrect"),
                    () -> assertEquals(DefaultCacheEntityClass.class, invocationOnMock.getArgument(5), "valueClass is incorrect"));
            return ((OptionalLoader)invocationOnMock.getArgument(1)).load(invocationOnMock.getArgument(0));
        });
        final Object actual = cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, Optional.of(new DefaultCacheEntityClass(key)), 1234L, key));
        assertEquals(Optional.of(new DefaultCacheEntityClass(1L)), actual);
    }

    @DisplayName("""
            The intercepted method, returning an Optional, has multiple params, none annotated as a key, exception is thrown.
             Method: DefaultCacheEntity defaultLoad(Long dummy, Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheOptionalNoAnnotatedKeyInParamsType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultOptionalLoad", Long.class, Long.class);

        final CacheInterceptorException actualException = assertThrows(CacheInterceptorException.class, () -> cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(1L), 1234L, 1L)));
        assertEquals("No parameter is on annotated with the 'CacheKey' annotation or is a cached bean for method defaultOptionalLoad in class, could not determine cache key.",
                actualException.getMessage());
    }

    @DisplayName("""
            The intercepted method has no params, Exception is thrown.
             Method: DefaultCacheEntity defaultLoad(Long dummy, Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheNoParamsType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultLoad");

        final CacheInterceptorException actualException = assertThrows(CacheInterceptorException.class, () -> cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(1L))));
        assertEquals("No parameters on method defaultLoad in class org.fermented.dairy.caches.interceptors.beans.CacheBean, could not determine cache key.",
                actualException.getMessage());
    }

    @DisplayName("""
            The intercepted method has void return type, Exception is thrown.
             Method: void loadVoid(Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheVoidReturnType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("loadVoid", Long.class);

        final CacheInterceptorException actualException = assertThrows(CacheInterceptorException.class, () -> cacheLoadInterceptor.loadIntoCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(1L))));
        assertEquals("void types cannot be cached",
                actualException.getMessage());
    }

}