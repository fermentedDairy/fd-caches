package org.fermented.dairy.caches.interceptors.beans;

import java.util.Optional;
import org.fermented.dairy.caches.interceptors.annotations.CacheDelete;
import org.fermented.dairy.caches.interceptors.annotations.CacheKey;
import org.fermented.dairy.caches.interceptors.annotations.CacheLoad;
import org.fermented.dairy.caches.interceptors.entities.DefaultCacheEntityClass;
import org.fermented.dairy.caches.interceptors.entities.NamedCachedBean;

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
    public DefaultCacheEntityClass defaultLoad(final Object dummy, @CacheKey final Long param) {
        return new DefaultCacheEntityClass(param);
    }

    @CacheLoad
    public DefaultCacheEntityClass defaultLoad(final Long dummy, final Long param) {
        return new DefaultCacheEntityClass(param);
    }

    @CacheLoad(optionalClass = DefaultCacheEntityClass.class)
    public Optional<DefaultCacheEntityClass> defaultOptionalLoad(final Long param){
        return Optional.of(new DefaultCacheEntityClass(param));
    }

    @CacheLoad(optionalClass = NamedCachedBean.class)
    public Optional<NamedCachedBean> namedOptionalLoad(final Long param){
        return Optional.of(new NamedCachedBean(param));
    }

    @CacheLoad(optionalClass = DefaultCacheEntityClass.class)
    public Optional<DefaultCacheEntityClass> defaultOptionalLoad(final Object dummy, @CacheKey final Long param){
        return Optional.of(new DefaultCacheEntityClass(param));
    }

    @CacheLoad(optionalClass = DefaultCacheEntityClass.class)
    public Optional<DefaultCacheEntityClass> defaultOptionalLoad(final Long dummy, final Long param){
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
    public void deleteFirst(Long key) {
        //No Op
    }

    @CacheDelete(deleteFirst = false)
    public void deleteAfter(Long key) {
        //No Op
    }

    @CacheDelete
    public DefaultCacheEntityClass deleteFirstResultKey(Long key){
        return new DefaultCacheEntityClass(key);
    }

    @CacheDelete(deleteFirst = false)
    public DefaultCacheEntityClass deleteAfterResultKey(Long key){
        return new DefaultCacheEntityClass(key);
    }

    @CacheDelete
    public void deleteFirst(Object dummy, @CacheKey Long key) {
        //No Op
    }

    @CacheDelete(deleteFirst = false)
    public void deleteAfter(Object dummy, @CacheKey Long key) {
        //no op
    }

    @CacheDelete(deleteFirst = false)
    public void deleteAfter(Long dummy,  Long key) {
        //No Op
    }

    @CacheDelete
    Optional<DefaultCacheEntityClass> deleteFirstOptional(Long key) {
        return Optional.of(new DefaultCacheEntityClass(key));
    }
}
