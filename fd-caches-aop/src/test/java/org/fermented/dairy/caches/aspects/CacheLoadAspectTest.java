package org.fermented.dairy.caches.aspects;

import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.fermented.dairy.caches.aspects.utils.AspectUtils;
import org.fermented.dairy.caches.interceptors.beans.CacheBean;
import org.fermented.dairy.caches.interceptors.entities.DefaultCacheEntityClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheLoadAspectTest {

    CacheLoadAspect cacheLoadAspect;

    @Mock
    CacheProvider defaultCacheProvider;

    @Mock
    CacheProvider cacheProvider1;

    @Mock
    CacheProvider cacheProvider2;

    @Mock
    Environment environment;

    List<CacheProvider> cacheProviderInstances;

    @BeforeEach
    void init() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        initInjectedCacheInstances();
    }

    void initInjectedCacheInstances() {
        cacheProviderInstances = List.of(
                cacheProvider1, cacheProvider2, defaultCacheProvider
        );
        lenient().when(defaultCacheProvider.getProviderName()).thenReturn("default");
        lenient().when(cacheProvider1.getProviderName()).thenReturn("cache1");
        lenient().when(cacheProvider2.getProviderName()).thenReturn("cache2");
        lenient().when(environment.getProperty("fd.config.cache.provider.default", String.class))
                .thenReturn("default");
        lenient().when(environment.getProperty("fd.config.cache.ttl.default", Long.class))
                .thenReturn(3000L);
        cacheLoadAspect = new CacheLoadAspect(environment, cacheProviderInstances);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param. Default cacheProvider is used.
             Method: DefaultCacheEntity defaultLoad(Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheCorrectKeyType() throws Throwable {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultLoad", Long.class);
        final Long key = 1L;
        when(defaultCacheProvider.load(
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
        final Object actual = cacheLoadAspect.loadIntoCache(AspectUtils.getProceedingJoinPoint(
                interceptedMethod,
                new DefaultCacheEntityClass(key),
                key));
        assertEquals(new DefaultCacheEntityClass(1L), actual);
    }

    @DisplayName("""
            The intercepted method has a single unannotated param. Cache and cacheProvider name from class is used.
             Method: NamedCacheBean namedLoad(Long param)
            """)
    @Test
    void singleUnannotatedOverriddenCacheCorrectKeyType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("namedLoad", Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has a single unannotated param. Cache, cacheProvider name and TTL overridden from config.
             Method: NamedCacheBean namedLoad(Long param)
            """)
    @Test
    void singleUnannotatedOverriddenCacheCorrectKeyTypeConfigOverrides() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("namedLoad", Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has a single unannotated param. Cache disabled in config
             Method: NamedCacheBean namedLoad(Long param)
            """)
    @Test
    void singleUnannotatedOverriddenCacheCorrectKeyTypeConfigCacheDisabled() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("namedLoad", Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has multiple params, one is annotated as a key. Param is the same type as annotated key member in DefaultCacheEntity, default cacheProvider is used.
             Method: DefaultCacheEntityClass defaultLoad(Object dummy, @CacheKey Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheCorrectAnnotatedKeyInParamsType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultLoad", Object.class, Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has multiple params, none annotated as a key, exception is thrown.
             Method: DefaultCacheEntity defaultLoad(Long dummy, Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheNoAnnotatedKeyInParamsType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultLoad", Long.class, Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Param is the same type as annotated key member in DefaultCacheEntity, default cacheProvider is used.
             Method: Optional<DefaultCacheEntity> defaultOptionalLoad(Long param)
            """)
    @Test
    void singleUnannotatedOptionalDefaultCacheCorrectKeyType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultOptionalLoad", Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Cache and cacheProvider name is used from annotation.
             Method: Optional<NamedCacheBean> namedOptionalLoad(Long param)
            """)
    @Test
    void singleUnannotatedOptionalNamedCacheCorrectKeyType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("namedOptionalLoad", Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Cache disabled in config.
             Method: Optional<NamedCachedBean> namedOptionalLoad(Long param)
            """)
    @Test
    void singleUnannotatedOptionalNamedCacheCorrectKeyTypeConfigDisabled() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("namedOptionalLoad", Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Cache and cacheProvider name is used from config.
             Method: Optional<NamedCacheBean> namedOptionalLoad(Long param)
            """)
    @Test
    void singleUnannotatedOptionalNamedCacheCorrectKeyTypeConfigOverrides() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("namedOptionalLoad", Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has a single unannotated param and Optional return. Param is the same type as annotated key member in DefaultCacheEntity, default cacheProvider is used.
             Method: Optional<DefaultCacheEntityClass> defaultOptionalLoad(Object dummy, @CacheKey Long param)
            """)
    @Test
    void singleUnannotatedOptionalDefaultCacheCorrectKeyAnnotatedType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultOptionalLoad", Object.class, Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method, returning an Optional, has multiple params, none annotated as a key, exception is thrown.
             Method: Optional<DefaultCacheEntityClass> defaultOptionalLoad(Long dummy, Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheOptionalNoAnnotatedKeyInParamsType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultOptionalLoad", Long.class, Long.class);
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has no params, Exception is thrown.
             Method: DefaultCacheEntity defaultLoad()
            """)
    @Test
    void singleUnannotatedDefaultCacheNoParamsType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultLoad");
        fail("Not Implemented");
    }

    @DisplayName("""
            The intercepted method has void return type, Exception is thrown.
             Method: void loadVoid(Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheVoidReturnType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("loadVoid", Long.class);
        fail("Not Implemented");
    }
}