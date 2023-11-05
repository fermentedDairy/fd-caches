package org.fermented.dairy.caches.rest.entity.records;

import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.fermented.dairy.caches.interceptors.annotations.CacheKey;
import org.fermented.dairy.caches.interceptors.annotations.Cached;

import java.util.UUID;

@Builder
@Schema
@Cached //Config in code is for defaults, config in config file redirects to its own cache provider
public record ConfigOverriddenCacheRecord(
        @Schema(required = true) @CacheKey UUID id,
        @Schema(required = true) String value) {
}
