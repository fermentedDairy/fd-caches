package org.fermented.dairy.caches.sb.aop.rest.entity.records;

import lombok.Builder;
import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;

import java.util.UUID;

/**
 * Cache Record for testing the default cache config.
 *
 * @param id Cache key
 * @param value value
 */
@Builder
@Cached
public record DefaultCacheRecord(@CacheKey UUID id, String value) {
}
