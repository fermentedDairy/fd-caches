package org.fermented.dairy.caches.sb.aop.rest.entity.rto.data;

import lombok.Builder;
import lombok.Singular;
import org.fermented.dairy.caches.sb.aop.rest.entity.rto.Link;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

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
