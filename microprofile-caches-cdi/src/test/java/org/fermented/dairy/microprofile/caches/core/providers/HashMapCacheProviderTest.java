package org.fermented.dairy.microprofile.caches.core.providers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.fermented.dairy.microprofile.caches.api.exceptions.CacheException;
import org.fermented.dairy.microprofile.caches.api.functions.Loader;
import org.fermented.dairy.microprofile.caches.api.interfaces.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HashMapCacheProviderTest {

    Cache provider = new HashMapCache();

    @BeforeEach
    void beforeEach(){
        provider.purge();
        assertTrue(provider.getCacheNames().isEmpty());
    }

    @DisplayName("""
    with an ampty cache\s
    given an initial load (non-optional)\s
    then second load should return the result of the first load\s
    (load cache miss followed by cache hit)
    """)
    @Test
    void cacheMissFollowedByCacheHit() {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final Loader<Object, Object> secondLoader = key -> "Number Should Come From Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        final String result2 = (String) provider.load(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
    with an empty cache\s
    given an initial load (non-optional), after the TTl has elapsed\s
    then second load should return the result of the first load\s
    (load cache miss followed by cache miss, expiry on load)
    """)
    @Test
    void cacheMissFollowedByCacheHitExpiryOnLoad() throws InterruptedException {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final Loader<Object, Object> secondLoader = key -> "Number Should Come From Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 30, Long.class, String.class);
        assertAll("assert initial load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
        Thread.sleep(50L);
        final String result2 = (String) provider.load(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertEquals(secondLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
    with an ampty cache\s
    given an initial load (non-optional), after the TTl has elapsed\s
    then state interrogation reflect expiry\s
    (load cache miss followed by cache miss, expiry on state interrogation)
    """)
    @Test
    void cacheMissFollowedByCacheHitExpiryOnStateInterrogation() throws InterruptedException {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final Loader<Object, Object> secondLoader = key -> "Number Should Come From Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 30, Long.class, String.class);
        assertAll("assert initial load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
        Thread.sleep(50L);
        assertAll("assert initial load results and cache state",
                () -> assertTrue(provider.getCacheNames().isEmpty()),
                () -> assertTrue(provider.getKeys("NumberCache").isEmpty()));
    }

    @DisplayName("""
    with an empty cache\s
    given an initial load (non-optional) \s
    then second load with incorrect cache key should throw exception\s
    (load cache miss followed by cache hit)
    """)
    @Test
    void cacheMissFollowedInvalidKeyTypeException() {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final Loader<Object, Object> secondLoader = key -> "Number Should Come From Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        CacheException exception = assertThrows(CacheException.class, () -> provider.load("1", secondLoader, "NumberCache", 3000, Long.class, String.class));
        assertEquals("java.lang.String is not a valid key class for cache NumberCache", exception.getMessage());
    }

    @DisplayName("""
    with an empty cache\s
    given an initial load (non-optional) \s
    then second load with incorrect cache key should throw exception\s
    (load cache miss followed by cache hit)
    """)
    @Test
    void cacheMissFollowedInvalidResultTypeException() {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final Loader<Object, Object> secondLoader = key -> Math.PI;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        final CacheException exception = assertThrows(CacheException.class, () -> provider.load(1L, secondLoader, "NumberCache", 3000, Long.class, String.class));
        assertEquals("java.lang.Double is not a valid result class for cache NumberCache", exception.getMessage());
    }
}