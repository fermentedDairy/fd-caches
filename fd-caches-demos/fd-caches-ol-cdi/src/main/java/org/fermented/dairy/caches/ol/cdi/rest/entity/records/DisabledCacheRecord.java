package org.fermented.dairy.caches.ol.cdi.rest.entity.records;

import java.util.UUID;
import lombok.Builder;
import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;

/**
 * Cache Record for testing caching disabled in config for a record.
 *
 * @param id Cache key
 * @param value value
 */
@Builder
@Cached
public record DisabledCacheRecord(@CacheKey UUID id, String value) {
}
