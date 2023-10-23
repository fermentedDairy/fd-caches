package org.fermented.dairy.caches.rest.entity.rto;

import lombok.Builder;

@Builder
public record Link(String href, String rel, String type) {
}
