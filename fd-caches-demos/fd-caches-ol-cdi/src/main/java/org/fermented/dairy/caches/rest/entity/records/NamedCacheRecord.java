package org.fermented.dairy.caches.rest.entity.records;

import lombok.Builder;
import org.fermented.dairy.caches.interceptors.annotations.CacheKey;
import org.fermented.dairy.caches.interceptors.annotations.Cached;

import java.util.UUID;

@Builder
@Cached(cacheProviderName = "namedCache", cacheName = "namedCacheRecords")
public record NamedCacheRecord(@CacheKey UUID id, String value) {
}
