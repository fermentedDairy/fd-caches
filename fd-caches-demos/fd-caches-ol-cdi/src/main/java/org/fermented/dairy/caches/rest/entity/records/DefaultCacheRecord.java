package org.fermented.dairy.caches.rest.entity.records;

import lombok.Builder;
import org.fermented.dairy.caches.interceptors.annotations.CacheKey;

import java.util.UUID;

@Builder
public record DefaultCacheRecord(@CacheKey UUID id, String value) {
}
