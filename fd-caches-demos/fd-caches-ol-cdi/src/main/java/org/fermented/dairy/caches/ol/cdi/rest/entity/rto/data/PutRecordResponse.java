package org.fermented.dairy.caches.ol.cdi.rest.entity.rto.data;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Singular;

/**
 * Response for putting record.
 *
 * @param id The ID of the object
 * @param links HATEOAS links
 */
@Builder
public record PutRecordResponse(UUID id, @Singular Set<Link> links) {

    /**
     * HATEOAS links.
     *
     * @return HATEOAS links
     */
    public Set<Link> links() {
        return Collections.unmodifiableSet(links);
    }
}
