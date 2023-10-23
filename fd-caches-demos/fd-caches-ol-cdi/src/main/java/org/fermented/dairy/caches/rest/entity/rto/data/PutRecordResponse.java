package org.fermented.dairy.caches.rest.entity.rto.data;

import lombok.Builder;
import lombok.Singular;
import org.fermented.dairy.caches.rest.entity.rto.Link;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Builder
public record PutRecordResponse(UUID id, @Singular Set<Link> links) {
    @Override
    public Set<Link> links() {
        return Collections.unmodifiableSet(links);
    }
}
