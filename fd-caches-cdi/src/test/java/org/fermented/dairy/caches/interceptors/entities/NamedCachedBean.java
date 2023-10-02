package org.fermented.dairy.caches.interceptors.entities;

import java.util.Objects;

import org.fermented.dairy.caches.interceptors.annotations.CacheKey;
import org.fermented.dairy.caches.interceptors.annotations.Cached;

@SuppressWarnings("MissingJavadoc")
@Cached(cacheProviderName = "cache1", cacheName = "overriddenCacheName", ttlMilliSeconds = 10)
public class NamedCachedBean {

    @CacheKey
    private final long id;

    public NamedCachedBean(final long id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final NamedCachedBean that = (NamedCachedBean) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NamedCachedBean{" + "id=" + id +
                '}';
    }
}
