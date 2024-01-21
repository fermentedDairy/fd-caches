package org.fermented.dairy.caches.sb.aop.rest.entity.rto;

import lombok.Builder;

/**
 * HATEOAS data.
 *
 * @param href location of the resource.
 * @param rel Relationship.
 * @param type HTTP operation type.
 */
@Builder
public record Link(String href, String rel, String type) {
}
