package org.fermented.dairy.caches.interceptors.beans;

import java.util.Optional;
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
}
