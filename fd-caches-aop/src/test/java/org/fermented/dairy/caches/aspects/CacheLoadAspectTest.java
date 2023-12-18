package org.fermented.dairy.caches.aspects;

import org.fermented.dairy.caches.interceptors.beans.CacheBean;
import org.fermented.dairy.caches.interceptors.entities.DefaultCacheEntityClass;
import org.fermented.dairy.caches.interceptors.entities.NamedCachedBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CacheLoadAspectTest {

    @DisplayName("""
            The intercepted method has a single unannotated param. Default cacheProvider is used.
             Method: DefaultCacheEntity defaultLoad(Long param)
            """)
    @Test
    void singleUnannotatedDefaultCacheCorrectKeyType() throws Exception {
        final Method interceptedMethod = CacheBean.class.getMethod("defaultLoad", Long.class);
        fail("Not Implemented");
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