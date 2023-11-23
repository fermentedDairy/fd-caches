package org.fermented.dairy.caches.interceptors.entities;

import java.util.Objects;
import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;

/**
 * Entity to test default provider caching
 */
@SuppressWarnings("MissingJavadoc")
@Cached
public class DefaultCacheEntityClass {

    @CacheKey
    private final Long key;

    public DefaultCacheEntityClass(final Long key) {
        this.key = key;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefaultCacheEntityClass that = (DefaultCacheEntityClass) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "DefaultCacheEntityClass{" + "key=" + key +
                '}';
    }
}
