package org.fermented.dairy.caches.rest.entity.records;

import java.util.UUID;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;

/**
 * Cache Record for testing the use of cache config from config.
 *
 * @param id Cache key
 * @param value value
 */
@Builder
@Schema
@Cached //Config in code is for defaults, config in config file redirects to its own cache provider
public record ConfigOverriddenCacheRecord(
        @Schema(required = true) @CacheKey UUID id,
        @Schema(required = true) String value) {
}
