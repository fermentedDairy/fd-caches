package org.fermented.dairy.caches.interceptors.beans;

import java.util.Optional;
import org.fermented.dairy.caches.interceptors.annotations.CacheDelete;
import org.fermented.dairy.caches.interceptors.annotations.CacheKey;
import org.fermented.dairy.caches.interceptors.annotations.CacheLoad;
import org.fermented.dairy.caches.interceptors.annotations.CachedType;
import org.fermented.dairy.caches.interceptors.entities.CacheRecord;
import org.fermented.dairy.caches.interceptors.entities.DefaultCacheEntityClass;
import org.fermented.dairy.caches.interceptors.entities.NamedCachedBean;

@SuppressWarnings({"MissingJavadoc", "unused", "LocalCanBeFinal"})
public class CacheBean {

    @CacheLoad
    public DefaultCacheEntityClass defaultLoad(final Long param){
        return new DefaultCacheEntityClass(param);
    }

    @CacheLoad
    public NamedCachedBean namedLoad(final Long param){
        return new NamedCachedBean(param);
    }

    @CacheLoad
    public DefaultCacheEntityClass defaultLoad(final Object ignoredDummy, @CacheKey final Long param) {
        return new DefaultCacheEntityClass(param);
    }

    @CacheLoad
    public DefaultCacheEntityClass defaultLoad(final Long ignoredDummy, final Long param) {
        return new DefaultCacheEntityClass(param);
    }

    @CacheLoad
    @CachedType(DefaultCacheEntityClass.class)
    public Optional<DefaultCacheEntityClass> defaultOptionalLoad(final Long param){
        return Optional.of(new DefaultCacheEntityClass(param));
    }

    @CacheLoad
    @CachedType(NamedCachedBean.class)
    public Optional<NamedCachedBean> namedOptionalLoad(final Long param){
        return Optional.of(new NamedCachedBean(param));
    }

    @CacheLoad
    @CachedType(DefaultCacheEntityClass.class)
    public Optional<DefaultCacheEntityClass> defaultOptionalLoad(final Object ignoredDummy, @CacheKey final Long param){
        return Optional.of(new DefaultCacheEntityClass(param));
    }

    @CacheLoad
    @CachedType(DefaultCacheEntityClass.class)
    public Optional<DefaultCacheEntityClass> defaultOptionalLoad(final Long ignoredDummy, final Long param){
        return Optional.of(new DefaultCacheEntityClass(param));
    }

    @CacheLoad
    public DefaultCacheEntityClass defaultLoad() {
        return new DefaultCacheEntityClass(1L);
    }

    @CacheLoad
    public void loadVoid(Long param)
    {
        //No Op
    }

    @CacheDelete
    @CachedType(DefaultCacheEntityClass.class)
    public DefaultCacheEntityClass deleteDefault(Long key) {
        return null;
    }

    @CacheDelete
    @CachedType(DefaultCacheEntityClass.class)
    public DefaultCacheEntityClass deleteDefault(Object ignoredDummy, @CacheKey Long key) {
        return null;
    }

    @CacheDelete
    @CachedType(DefaultCacheEntityClass.class)
    public DefaultCacheEntityClass deleteDefault(Long ignoredDummy, Long key) {
        return null;
    }

    @CacheDelete
    @CachedType(NamedCachedBean.class)
    public NamedCachedBean deleteNamed(Long key) {
        return null;
    }

    @CacheDelete
    @CachedType(NamedCachedBean.class)
    public NamedCachedBean deleteNamed(Object ignoredDummy, @CacheKey Long key) {
        return null;
    }

    @CacheDelete
    @CachedType(NamedCachedBean.class)
    public NamedCachedBean deleteNamed(Long ignoredDummy, Long key) {
        return null;
    }

    @CacheDelete
    @CachedType(DefaultCacheEntityClass.class)
    public DefaultCacheEntityClass deleteDefault(DefaultCacheEntityClass toDelete) {
        return null;
    }

    @CacheDelete
    @CachedType(DefaultCacheEntityClass.class)
    public DefaultCacheEntityClass deleteDefault(Long ignoredDummy, DefaultCacheEntityClass toDelete) {
        return null;
    }

    @CacheDelete
    public DefaultCacheEntityClass deleteNamed(NamedCachedBean toDelete) {
        return null;
    }

    @CacheDelete
    public DefaultCacheEntityClass deleteNamed(Long ignoredDummy, NamedCachedBean toDelete) {
        return null;
    }

    @CacheDelete
    public DefaultCacheEntityClass deleteRecord(CacheRecord toDelete) {
        return null;
    }

    @CacheDelete
    public DefaultCacheEntityClass deleteRecord(Long ignoredDummy, CacheRecord toDelete) {
        return null;
    }
}
