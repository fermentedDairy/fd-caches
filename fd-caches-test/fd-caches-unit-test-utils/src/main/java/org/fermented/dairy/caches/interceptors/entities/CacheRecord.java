package org.fermented.dairy.caches.interceptors.entities;

import org.fermented.dairy.caches.annotations.CacheKey;
import org.fermented.dairy.caches.annotations.Cached;

@SuppressWarnings("MissingJavadoc")
@Cached
public record CacheRecord(@CacheKey Long id) {}
