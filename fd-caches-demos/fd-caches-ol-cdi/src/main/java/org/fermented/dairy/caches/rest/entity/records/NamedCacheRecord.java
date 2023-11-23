package org.fermented.dairy.caches.rest.entity.records;

import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;

import java.util.UUID;

@Schema
@Builder
@Cached(cacheProviderName = "namedCache", cacheName = "namedCacheRecords")
public record NamedCacheRecord(
        @Schema(required = true) @CacheKey UUID id,
        @Schema(required = true) String value) {
}
