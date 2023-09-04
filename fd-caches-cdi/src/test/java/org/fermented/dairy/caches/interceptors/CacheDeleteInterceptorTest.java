package org.fermented.dairy.caches.interceptors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import jakarta.enterprise.inject.Instance;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.eclipse.microprofile.config.Config;
import org.fermented.dairy.caches.api.interfaces.Cache;
import org.fermented.dairy.caches.interceptors.beans.CacheBean;
import org.fermented.dairy.caches.interceptors.entities.DefaultCacheEntityClass;
import org.fermented.dairy.caches.interceptors.exceptions.CacheInterceptorException;
import org.fermented.dairy.caches.interceptors.utils.ContextUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


    //Single Param
    @DisplayName("""
            Deleting first with single unannotated parameter as the key.
            Method void deleteFirst(Long key)
            """)
    @Test
    void deletingFirstWithSingleUnannotatedParameterAsTheKey() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("deleteFirst", Long.class);
        final Long key = 1L;
        final InvocationContext ctx = ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), key);
        InOrder inOrder = inOrder(defaultCache, ctx);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ctx);
        assertEquals(new DefaultCacheEntityClass(1L), actual);
        inOrder.verify(defaultCache).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), key);
        inOrder.verify(ctx).proceed();
    }

    @DisplayName("""
            Deleting after with single unannotated parameter as the key.
            Method void deleteAfter(Long key)
            """)
    @Test
    void deletingAfterWithSingleUnannotatedParameterAsTheKey() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("deleteAfter", Long.class);
        final Long key = 1L;
        final InvocationContext ctx = ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), key);
        InOrder inOrder = inOrder(defaultCache, ctx);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ctx);
        assertEquals(new DefaultCacheEntityClass(1L), actual);
        inOrder.verify(ctx).proceed();
        inOrder.verify(defaultCache).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), key);
    }

    //Key in result field
    @DisplayName("""
            Deleting first with a field in the result as the key, exception thrown.
            Method DefaultCacheEntity deleteFirstUsingResult(Long key)
            """)
    @Test
    void deletingFirstWithResultFieldAsTheKey() throws Exception{
        final Method interceptedMethod = CacheBean.class.getMethod("deleteFirstResultKey", Long.class);
        final Long key = 1L;

        CacheInterceptorException cacheInterceptorException = assertThrows(CacheInterceptorException.class,
                () -> cacheDeleteInterceptor.deleteFromCache(ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(123456L), key)));
        assertEquals("Can only use cache result as a key source if deleteFirst is false", cacheInterceptorException.getMessage());
        verify(defaultCache, never()).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), key);
    }

    @DisplayName("""
            Deleting after with a field in the result as the key.
            Method DefaultCacheEntity deleteAfterUsingResult(Long key)
            """)
    @Test
    void deletingAfterWithResultFieldAsTheKey() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("deleteAfterResultKey", Long.class);
        final Long key = 1L;
        final InvocationContext ctx = ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(123456L), key);
        InOrder inOrder = inOrder(defaultCache, ctx);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ctx);
        assertEquals(new DefaultCacheEntityClass(123456L), actual);
        inOrder.verify(ctx).proceed();
        inOrder.verify(defaultCache).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), 123456L);
    }

    //Multiple keys, one annotated
    @DisplayName("""
            Deleting first with multiple params, one unannotated parameter as the key.
            Method void deleteFirst(Object dummy, @CacheKey Long key)
            """)
    @Test
    void deletingFirstWithMultipleParamsOneUnannotatedParameterAsTheKey() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("deleteFirst", Object.class, Long.class);
        final Long key = 1L;
        final InvocationContext ctx = ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), new Object(), key);
        InOrder inOrder = inOrder(defaultCache, ctx);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ctx);
        assertEquals(new DefaultCacheEntityClass(1L), actual);
        inOrder.verify(defaultCache).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), key);
        inOrder.verify(ctx).proceed();
    }

    @DisplayName("""
            Deleting after with multiple params, one unannotated parameter as the key.
            Method void deleteAfter(Object dummy, @CacheKey Long key)
            """)
    @Test
    void deletingAfterWithMultipleParamsOneUnannotatedParameterAsTheKey() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("deleteAfter", Object.class, Long.class);
        final Long key = 1L;
        final InvocationContext ctx = ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), new Object(), key);
        InOrder inOrder = inOrder(defaultCache, ctx);
        final Object actual = cacheDeleteInterceptor.deleteFromCache(ctx);
        assertEquals(new DefaultCacheEntityClass(1L), actual);

        inOrder.verify(ctx).proceed();
        inOrder.verify(defaultCache).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), key);
    }

    @DisplayName("""
            Deleting with no annotated params, exception thrown.
            Method void deleteAfter(long dummy,  Long key)
            """)
    @Test
    void deletingWithNoAnnotatedParamsExceptionThrown() throws Exception{
        final Method interceptedMethod = CacheBean.class.getMethod("deleteAfter", Long.class, Long.class);
        final Long key = 1L;
        final InvocationContext ctx = ContextUtils.getInvocationContext(interceptedMethod, new DefaultCacheEntityClass(key), new Object(), key);
        CacheInterceptorException cacheInterceptorException = assertThrows(CacheInterceptorException.class, () -> cacheDeleteInterceptor.deleteFromCache(ctx));
        assertEquals("At least one parameter must be annotated as teh cache key if useResultForKey is false", cacheInterceptorException.getMessage());

        verify(defaultCache, never()).removeValue(DefaultCacheEntityClass.class.getCanonicalName(), key);
    }

    //Single Param
    @DisplayName("""
            Deleting first with single unannotated parameter as the key. Optional result
            Method Optional<DefaultCacheEntity> deleteFirstOptional(Long key)
            """)
    @Test
    void deletingFirstWithSingleUnannotatedParameterAsTheKey_OptionalResult(){
        fail("Not implemented");
    }

    @DisplayName("""
            Deleting after with single unannotated parameter as the key. Optional result
            Method Optional<DefaultCacheEntity> deleteAfter(Long key)
            """)
    @Test
    void deletingAfterWithSingleUnannotatedParameterAsTheKey_OptionalResult(){
        fail("Not implemented");
    }

    //Key in result field
    @DisplayName("""
            Deleting first with result field as the key, exception thrown. Optional result
            Method Optional<DefaultCacheEntity> deleteFirstUsingResult(Long key)
            """)
    @Test
    void deletingFirstWithResultFieldAsTheKey_OptionalResult(){
        fail("Not implemented");
    }

    @DisplayName("""
            Deleting after with result field as the key. Optional result
            Method Optional<DefaultCacheEntity> deleteAfterUsingResult(Long key)
            """)
    @Test
    void deletingAfterWithResultFieldAsTheKey_OptionalResult(){
        fail("Not implemented");
    }

    //Multiple keys, one annotated
    @DisplayName("""
            Deleting first with multiple params, one unannotated parameter as the key. Optional result
            Method Optional<DefaultCacheEntity> deleteFirst(Object dummy, @CacheKey Long key)
            """)
    @Test
    void deletingFirstWithMultipleParamsOneUnannotatedParameterAsTheKey_OptionalResult(){
        fail("Not implemented");
    }

    @DisplayName("""
            Deleting after with multiple params, one unannotated parameter as the key. Optional Result
            Method Optional<DefaultCacheEntity> deleteAfter(Object dummy, @CacheKey Long key)
            """)
    @Test
    void deletingAfterWithMultipleParamsOneUnannotatedParameterAsTheKey_OptionalResult(){
        fail("Not implemented");
    }

    @DisplayName("""
            Deleting with no annotated params, exception thrown. Optional result
            Method Optional<DefaultCacheEntity> deleteAfter(long dummy,  Long key)
            """)
    @Test
    void deletingWithNoAnnotatedParamsExceptionThrown_OptionalResult(){
        fail("Not implemented");
    }

    //Cache defined by annotation
    @DisplayName("""
            Deleting first with single unannotated parameter as the key. Cache defined on bean
            Method NamedCacheBean deleteFirst(Long key)
            """)
    @Test
    void deletingFirstWithSingleUnannotatedParameterAsTheKeyCacheDefinedOnBean(){
        fail("Not implemented");
    }

    //Cache defined by annotation
    @DisplayName("""
            Deleting first with single unannotated parameter as the key. Optional Result and cache defined on bean
            Method Optional<NamedCacheBean> deleteFirst(Long key)
            """)
    @Test
    void deletingFirstWithSingleUnannotatedParameterAsTheKeyCacheDefinedOnBean_OptionalResult(){
        fail("Not implemented");
    }

    //Cache defined by annotation
    @DisplayName("""
            Deleting first with single unannotated parameter as the key. Cache defined by config
            Method NamedCacheBean deleteFirst(Long key)
            """)
    @Test
    void deletingFirstWithSingleUnannotatedParameterAsTheKeyCacheDefinedByConfig(){
        fail("Not implemented");
    }

    //Cache defined by annotation
    @DisplayName("""
            Deleting first with single unannotated parameter as the key. Optional Result and cache defined by config
            Method Optional<NamedCacheBean> deleteFirst(Long key)
            """)
    @Test
    void deletingFirstWithSingleUnannotatedParameterAsTheKeyCacheDefinedByConfig_OptionalResult(){
        fail("Not implemented");
    }

    //Cache defined by annotation
    @DisplayName("""
            Deleting first with single unannotated parameter as the key. Optional Result and cache disabled in config
            Method Optional<NamedCacheBean> deleteFirst(Long key)
            """)
    @Test
    void deletingFirstWithSingleUnannotatedParameterAsTheKeyCacheDisabledInConfig_OptionalResult(){
        fail("Not implemented");
    }

    //Cache defined by annotation
    @DisplayName("""
            Deleting first with single unannotated parameter as the key. Optional Result and cache defined default in cofnig
            Method Optional<DefaultCacheEntityBean> deleteFirst(Long key)
            """)
    @Test
    void deletingFirstWithSingleUnannotatedParameterAsTheKeyDefaultCacheDefinedByConfig_OptionalResult(){
        fail("Not implemented");
    }

    //Cache defined by annotation
    @DisplayName("""
            Deleting first with single unannotated parameter as the key. Optional Result and cache defined default in cofnig
            Method Optional<DefaultCacheEntityBean> deleteFirst(Long key)
            """)
    @Test
    void deletingFirstWithSingleUnannotatedParameterAsTheKeyDefaultCacheDisabledInConfig_OptionalResult(){
        fail("Not implemented");
    }
}