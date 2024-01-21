package org.fermented.dairy.caches.sb.aop.rest.entity.records;

import lombok.Builder;
import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;

import java.util.UUID;

/**
 * Cache Record for testing the use of cache config from config.
 *
 * @param id Cache key
 * @param value value
 */
@Builder
@Cached //Config in code is for defaults, config in config file redirects to its own cache provider
public record ConfigOverriddenCacheRecord(
        @CacheKey UUID id, String value) {
}
