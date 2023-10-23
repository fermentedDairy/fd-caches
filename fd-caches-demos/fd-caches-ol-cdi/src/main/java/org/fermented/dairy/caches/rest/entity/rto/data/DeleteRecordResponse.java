package org.fermented.dairy.caches.rest.entity.rto.data;

import java.util.UUID;
import lombok.Builder;

@Builder
public record DeleteRecordResponse(UUID id) {
}
