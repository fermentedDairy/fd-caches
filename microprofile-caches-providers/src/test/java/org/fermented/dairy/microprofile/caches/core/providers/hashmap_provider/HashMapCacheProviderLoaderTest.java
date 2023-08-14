package org.fermented.dairy.microprofile.caches.core.providers.hashmap_provider;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.fermented.dairy.microprofile.caches.api.exceptions.CacheException;
import org.fermented.dairy.microprofile.caches.api.functions.Loader;
import org.fermented.dairy.microprofile.caches.api.interfaces.Cache;
import org.fermented.dairy.microprofile.caches.core.providers.HashMapCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HashMapCacheProviderLoaderTest {

    Cache provider = new HashMapCache();

    @BeforeEach
    void beforeEach() {
        provider.purge();
        assertTrue(provider.getCacheNames().isEmpty());
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (non-optional)\s
            then second load should return the result of the first load\s
            (load cache miss followed by cache hit)
            """)
    @Test
    void cacheMissFollowedByCacheHit() throws Exception {
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
            given an initial load (non-optional)\s
            then second load (value based) should return the result of the first load\s
            (load cache miss followed by value cache hit)
            """)
    @Test
    void cacheMissFollowedByValueCacheHit() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        final String result2 = (String) provider.load(1L, "Number Should Come From Cache: 1", "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (non-optional) that returns null from loader\s
            then null should be returned and nothing cached\s
            (load cache miss null from loader)
            """)
    @Test
    void cacheMissFNullFromLoader() throws Exception {
        final Loader<Object, Object> firstLoader = key -> null;
        final String result = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertNull(result),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertTrue(provider.getKeys("NumberCache").isEmpty()));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (non-optional), after the TTL has elapsed\s
            then second load should return the result of the first load\s
            (load cache miss followed by cache miss, expiry on load)
            """)
    @Test
    void cacheMissFollowedByCacheHitExpiryOnLoad() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final Loader<Object, Object> secondLoader = key -> "Number Should Come From Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 30, Long.class, String.class);
        assertAll("assert initial load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
        Thread.sleep(50L); //NOSONAR: TODO: is there a better way?
        final String result2 = (String) provider.load(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertEquals(secondLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (non-optional), after the TTL has elapsed\s
            then state interrogation reflect expiry\s
            (load cache miss followed by cache miss, expiry on state interrogation)
            """)
    @Test
    void cacheMissFollowedByCacheHitExpiryOnStateInterrogation() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 30, Long.class, String.class);
        assertAll("assert initial load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
        Thread.sleep(50L); //NOSONAR: TODO: Is there a better way?
        assertAll("assert initial load results and cache state",
                () -> assertTrue(provider.getKeys("NumberCache").isEmpty()));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (non-optional) with incorrect key type\s
            then throw an exception\s
            (load cache miss followed by cache hit)
            """)
    @Test
    void cacheMissFollowedInvalidKeyTypeException() {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final CacheException exception = assertThrows(CacheException.class, () -> provider.load("1", firstLoader, "NumberCache", 3000, Long.class, String.class));
        assertEquals("java.lang.String is not a valid key class for cache NumberCache", exception.getMessage());
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (non-optional) with incorrect return type\s
            then throw an exception\s
            (load cache miss followed by cache hit)
            """)
    @Test
    void cacheMissFollowedInvalidResultTypeException() {
        final Loader<Object, Object> loader = key -> Math.PI;
        final CacheException exception = assertThrows(CacheException.class, () -> provider.load(1L, loader, "NumberCache", 3000, Long.class, String.class));
        assertEquals("java.lang.Double is not a valid result class for cache NumberCache", exception.getMessage());
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (non-optional) concurrently with a second load\s
            then the first load value should be loaded into the cache and returned both times\s
            (concurrent load with same key thread locking with same key)
            """)
    @Test
    void concurrentLoadWithSameKeyBlocksAllThreads() throws Exception {
        final Loader<Object, Object> firstLoader = key -> {
            Thread.sleep(1000L); //NOSONAR: java:S2925 simulating latency in load function
            return "Number Loaded Into Cache: " + key;
        };
        final Loader<Object, Object> secondLoader = key -> "Number Should Come From Cache: " + key;

        final Future<String> futureResult1 = Executors.newSingleThreadExecutor().submit(
                () -> (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class));
        Thread.sleep(50L); ///NOSONAR: TODO: Is there a better way?
        final String result2 = (String) provider.load(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        final String result1 = futureResult1.get();
        assertAll("assert load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));

    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (non-optional)\s
            then remove value from load\s
            verify that only removed key has been removed\s
            (load cache miss followed by remove)
            """)
    @Test
    void cacheMissFollowedByValueRemoval() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        final String result2 = (String) provider.load(2L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(2L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L, 2L), provider.getKeys("NumberCache")));
        provider.removeValue("NumberCache", 1L);
        assertAll("assert cache state after remove",
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(2L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (non-optional)\s
            then clear cache\s
            (load cache miss followed by remove)
            """)
    @Test
    void cacheMissFollowedByPurge() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
        provider.clearCache("NumberCache");
        assertAll("assert cache state after remove",
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            calling getProviderName should return name from implementation
            """)
    @Test
    void providerNameFromImplementation() {
       assertEquals(HashMapCache.class.getCanonicalName(), provider.getProviderName());
    }
}