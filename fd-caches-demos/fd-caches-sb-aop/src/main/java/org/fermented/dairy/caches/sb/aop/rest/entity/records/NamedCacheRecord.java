package org.fermented.dairy.caches.sb.aop.rest.entity.records;

import lombok.Builder;
import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;

import java.util.UUID;

/**
 * Cache Record for testing caching configured in the annotation.
 *
 * @param id Cache key
 * @param value value
 */
@Builder
@Cached(cacheProviderName = "namedCache", cacheName = "namedCacheRecords")
public record NamedCacheRecord(@CacheKey UUID id, String value) {
}
