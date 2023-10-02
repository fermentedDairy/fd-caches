package org.fermented.dairy.caches.interceptors.entities;

import org.fermented.dairy.caches.interceptors.annotations.CacheKey;
import org.fermented.dairy.caches.interceptors.annotations.Cached;

@SuppressWarnings("MissingJavadoc")
@Cached
public record CacheRecord(@CacheKey Long id) {}
