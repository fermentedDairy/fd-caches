package org.fermented.dairy.caches.rest.entity.records;

import lombok.Builder;
import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;

import java.util.UUID;

@Builder
@Cached
public record DisabledCacheRecord(@CacheKey UUID id, String value) {
}
