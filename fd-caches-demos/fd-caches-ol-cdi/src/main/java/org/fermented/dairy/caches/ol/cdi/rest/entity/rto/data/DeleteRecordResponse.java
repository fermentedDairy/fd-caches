package org.fermented.dairy.caches.ol.cdi.rest.entity.rto.data;

import java.util.UUID;
import lombok.Builder;

/**
 * Response when deleting a record.
 *
 * @param id The ID of the deleted record
 */
@Builder
public record DeleteRecordResponse(UUID id) {
}
