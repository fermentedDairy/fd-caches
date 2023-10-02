package org.fermented.dairy.caches.interceptors;

import jakarta.enterprise.inject.Instance;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.eclipse.microprofile.config.Config;
import org.fermented.dairy.caches.api.interfaces.Cache;
import org.fermented.dairy.caches.interceptors.beans.CacheBean;
import org.fermented.dairy.caches.interceptors.entities.CacheRecord;
import org.fermented.dairy.caches.interceptors.entities.DefaultCacheEntityClass;
import org.fermented.dairy.caches.interceptors.entities.NamedCachedBean;
import org.fermented.dairy.caches.interceptors.exceptions.CacheInterceptorException;
import org.fermented.dairy.caches.interceptors.utils.ContextUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheDeleteInterceptorTest {

    @InjectMocks
    CacheDeleteInterceptor cacheDeleteInterceptor;

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
        initInjectedProperties();
        initInjectedCacheInstances();
    }

    void initInjectedCacheInstances() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        cacheInstances = List.of(
                cache1, cache2, defaultCache
        );
        lenient().when(defaultCache.getProviderName()).thenReturn("default");
        lenient().when(cache1.getProviderName()).thenReturn("cache1");
        lenient().when(cache2.getProviderName()).thenReturn("cache2");
        lenient().when(providers.iterator()).thenReturn(cacheInstances.iterator());
        lenient().when(providers.stream()).thenReturn(cacheInstances.stream());
        MethodUtils.invokeMethod(cacheDeleteInterceptor, true, "init");
    }

    void initInjectedProperties() throws IllegalAccessException {
        FieldUtils.writeField(cacheDeleteInterceptor, "defaultProviderName", "default", true);
        FieldUtils.writeField(cacheDeleteInterceptor, "defaultTtl", 3000, true);
    }

    @DisplayName("""
            The intercepted method has a single parameter, delete in default cache
             Method: DefaultCacheEntityClass deleteDefault(Long key)\s
            """)
    @Test
    void defaultDeleteSingleParam() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteDefault", Long.class);
        final DefaultCacheEntityClass expectedObject = new DefaultCacheEntityClass(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ContextUtils.getInvocationContext(interceptedMethod, expectedObject, 1L));
        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(defaultCache).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), 1L);
    }

    @DisplayName("""
            The intercepted method has 2 parameters, one annotated as the key, delete in default cache
             Method: DefaultCacheEntityClass deleteDefault(Object dummy, @CacheKey Long key)\s
            """)
    @Test
    void defaultDeleteMultipleParamWithAnnotation() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteDefault", Object.class, Long.class);

        final DefaultCacheEntityClass expectedObject = new DefaultCacheEntityClass(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ContextUtils.getInvocationContext(interceptedMethod, expectedObject, new Object(), 1L));

        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(defaultCache).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), 1L);
    }

    @DisplayName("""
            The intercepted method has 2 parameters, none annotated as the key, exception thrown
             Method: void deleteDefault(Long dummy, Long key)\s
            """)
    @Test
    void defaultDeleteMultipleParamWithoutAnnotation() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("deleteDefault", Long.class, Long.class);
        final CacheInterceptorException actualException = assertThrows(CacheInterceptorException.class,
                () -> cacheDeleteInterceptor.deleteFromCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(1L), 2L, 1L)));
        verify(defaultCache, never()).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), 1L);
        assertEquals("No parameter is on annotated with the 'CacheKey' annotation or is a cached bean for method deleteDefault in class, could not determine cache key.",
                actualException.getMessage());
    }

    @DisplayName("""
            The intercepted method has a single parameter, delete in named cache
             Method: void deleteDefault(Long key)\s
            """)
    @Test
    void namedDeleteSingleParam() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteNamed", Long.class);
        final NamedCachedBean expectedObject = new NamedCachedBean(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ContextUtils.getInvocationContext(interceptedMethod, expectedObject, 1L));
        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(cache1).removeValue("overriddenCacheName", 1L);
    }

    @DisplayName("""
            The intercepted method has 2 parameters, one annotated as the key, delete in named cache
             Method: void deleteDefault(Object dummy, @CacheKey Long key)\s
            """)
    @Test
    void namedDeleteMultipleParamWithAnnotation() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteNamed", Object.class, Long.class);
        final NamedCachedBean expectedObject = new NamedCachedBean(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ContextUtils.getInvocationContext(interceptedMethod, expectedObject, new Object(), 1L));

        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(cache1).removeValue("overriddenCacheName", 1L);
    }

    @DisplayName("""
            The intercepted method has 2 parameters, none annotated as the key, exception thrown
             Method: void deleteDefault(Long dummy, Long key)\s
            """)
    @Test
    void namedDeleteMultipleParamWithoutAnnotation() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("deleteNamed", Long.class, Long.class);
        final CacheInterceptorException actualException = assertThrows(CacheInterceptorException.class,
                () -> cacheDeleteInterceptor.deleteFromCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(1L), new Object(), 1L)));
        verify(defaultCache, never()).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), 1L);
        assertEquals("No parameter is on annotated with the 'CacheKey' annotation or is a cached bean for method deleteNamed in class, could not determine cache key.",
                actualException.getMessage());
    }

    @DisplayName("""
            The intercepted method has a single parameter, delete in configured cache
             Method: void deleteDefault(Long key)\s
            """)
    @Test
    void configuredDeleteSingleParam() throws Exception {

        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cacheprovider", String.class)).thenReturn(Optional.of("cache2"));
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cachename", String.class)).thenReturn(Optional.of("configuredCacheName"));


        final Method interceptedMethod = CacheBean.class.getMethod("deleteNamed", Long.class);

        final NamedCachedBean expectedObject = new NamedCachedBean(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ContextUtils.getInvocationContext(interceptedMethod, expectedObject, 1L));
        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(cache2).removeValue("configuredCacheName", 1L);
    }

    @DisplayName("""
            The intercepted method has 2 parameters, one annotated as the key, delete in configured cache
             Method: void deleteDefault(Object dummy, @CacheKey Long key)\s
            """)
    @Test
    void configuredDeleteMultipleParamWithAnnotation() throws Exception {

        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cacheprovider", String.class)).thenReturn(Optional.of("cache2"));
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cachename", String.class)).thenReturn(Optional.of("configuredCacheName"));

        final Method interceptedMethod = CacheBean.class.getMethod("deleteNamed", Object.class, Long.class);
        final NamedCachedBean expectedObject = new NamedCachedBean(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ContextUtils.getInvocationContext(interceptedMethod, expectedObject, new Object(), 1L));

        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(cache2).removeValue("configuredCacheName", 1L);
    }

    @DisplayName("""
            The intercepted method has a single parameter that is a cached bean, delete in default cache
             Method: DefaultCacheEntityClass deleteDefault(DefaultCacheEntityClass key)\s
            """)
    @Test
    void defaultDeleteSingleParamCachedObject() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteDefault", DefaultCacheEntityClass.class);
        final DefaultCacheEntityClass expectedObject = new DefaultCacheEntityClass(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(
                ContextUtils.getInvocationContext(
                        interceptedMethod,
                        expectedObject,
                        expectedObject)
        );
        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(defaultCache).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), 1L);
    }

    @DisplayName("""
            The intercepted method has 2 parameters, one annotated as cached bean delete from default cache
             Method: DefaultCacheEntityClass deleteDefault(Object dummy, DefaultCacheEntityClass key)\s
            """)
    @Test
    void defaultDeleteMultipleParamNoneIsCachedBean() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteDefault", Long.class, DefaultCacheEntityClass.class);

        final DefaultCacheEntityClass expectedObject = new DefaultCacheEntityClass(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(
                ContextUtils.getInvocationContext(
                        interceptedMethod,
                        expectedObject,
                        new Object(),
                        1L));
        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(defaultCache).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), 1L);
    }

    @DisplayName("""
            The intercepted method has a single parameter that is a named cached bean, delete in named cache
             Method: DefaultCacheEntityClass deleteDefault(DefaultCacheEntityClass key)\s
            """)
    @Test
    void defaultDeleteSingleParamNamedCachedObject() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteNamed", NamedCachedBean.class);
        final NamedCachedBean expectedObject = new NamedCachedBean(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(
                ContextUtils.getInvocationContext(
                        interceptedMethod,
                        expectedObject,
                        expectedObject)
        );
        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(cache1).removeValue("overriddenCacheName", 1L);
    }

    @DisplayName("""
            The intercepted method has 2 parameters, one is a named cached bean, delete in named cache
             Method: DefaultCacheEntityClass deleteDefault(Object dummy, DefaultCacheEntityClass key)\s
            """)
    @Test
    void defaultDeleteMultipleParamOneIsNamedCachedBean() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteNamed", Long.class, NamedCachedBean.class);

        final NamedCachedBean expectedObject = new NamedCachedBean(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(
                ContextUtils.getInvocationContext(
                        interceptedMethod,
                        expectedObject,
                        new Object(),
                        expectedObject)
        );

        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(cache1).removeValue("overriddenCacheName", 1L);
    }

    @DisplayName("""
            The intercepted method has a single parameter that is a named configured cached bean, delete in named configured cache
             Method: DefaultCacheEntityClass deleteDefault(DefaultCacheEntityClass key)\s
            """)
    @Test
    void defaultDeleteSingleParamNamedConfiguredCachedObject() throws Exception {

        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cacheprovider", String.class)).thenReturn(Optional.of("cache2"));
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cachename", String.class)).thenReturn(Optional.of("configuredCacheName"));

        final Method interceptedMethod = CacheBean.class.getMethod("deleteNamed", NamedCachedBean.class);
        final NamedCachedBean expectedObject = new NamedCachedBean(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(
                ContextUtils.getInvocationContext(
                        interceptedMethod,
                        expectedObject,
                        expectedObject)
        );
        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(cache2).removeValue("configuredCacheName", 1L);
    }

    @DisplayName("""
            The intercepted method has 2 parameters, one is a named cached bean with config, delete in named configured cache
             Method: DefaultCacheEntityClass deleteDefault(Object dummy, DefaultCacheEntityClass key)\s
            """)
    @Test
    void defaultDeleteMultipleParamOneIsNamedConfiguredCachedBean() throws Exception {

        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cacheprovider", String.class)).thenReturn(Optional.of("cache2"));
        when(config.getOptionalValue("fd.config.cache." + NamedCachedBean.class.getCanonicalName() + ".cachename", String.class)).thenReturn(Optional.of("configuredCacheName"));

        final Method interceptedMethod = CacheBean.class.getMethod("deleteNamed", Long.class, NamedCachedBean.class);

        final NamedCachedBean expectedObject = new NamedCachedBean(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(
                ContextUtils.getInvocationContext(
                        interceptedMethod,
                        expectedObject,
                        new Object(),
                        expectedObject)
        );

        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(cache2).removeValue("configuredCacheName", 1L);
    }

    @DisplayName("""
            The intercepted method has a single parameter that is a cached record, delete in default cache
             Method: DefaultCacheEntityClass deleteDefault(CacheRecord key)\s
            """)
    @Test
    void defaultDeleteSingleParamDefaultCachedRecord() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteRecord", CacheRecord.class);
        final CacheRecord expectedObject = new CacheRecord(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(
                ContextUtils.getInvocationContext(
                        interceptedMethod,
                        expectedObject,
                        expectedObject)
        );
        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(defaultCache).removeValue(CacheRecord.class.getCanonicalName(), 1L);
    }

    @DisplayName("""
            The intercepted method has multiple parameters, one is a cached record, delete in default cache
             Method: DefaultCacheEntityClass deleteDefault(Long dummy, DefaultCacheEntityClass key)\s
            """)
    @Test
    void defaultDeleteMultipleParamDefaultCachedRecord() throws Exception {

        final Method interceptedMethod = CacheBean.class.getMethod("deleteRecord", Long.class, CacheRecord.class);
        final CacheRecord expectedObject = new CacheRecord(1L);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(
                ContextUtils.getInvocationContext(
                        interceptedMethod,
                        expectedObject,
                        expectedObject)
        );
        assertSame(actual, expectedObject, "Incorrect object returned");
        verify(defaultCache).removeValue(CacheRecord.class.getCanonicalName(), 1L);
    }
}