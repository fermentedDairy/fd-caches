package org.fermented.dairy.caches.sb.aop.rest.entity.rto.data;

import lombok.Builder;

import java.util.UUID;

/**
 * Response when deleting a record.
 *
 * @param id The ID of the deleted record
 */
@Builder
public record DeleteRecordResponse(UUID id) {
}
