package org.fermented.dairy.caches.providers.hashmap_provider;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.fermented.dairy.caches.api.exceptions.CacheException;
import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.fermented.dairy.caches.providers.HashMapCacheProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HashMapCacheProviderProviderLoaderTest {

    CacheProvider provider = new HashMapCacheProvider();

    @BeforeEach
    void beforeEach() {
        provider.purge();
        assertTrue(provider.getCacheNames().isEmpty());
    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional)
             then second load should return the result of the first load
             and peek should return cached object
             (load cacheProvider miss followed by cacheProvider hit)
            """)
    @Test
    void cacheMissFollowedByCacheHit() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final Loader<Object, Object> secondLoader = key -> "Number Should Come From Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        final String result2 = (String) provider.load(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cacheProvider state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(1L), result2),
                () -> {
                    final Optional<?> peaked = provider.peek("NumberCache", 1L);
                    assertTrue(peaked.isPresent());
                    assertEquals(firstLoader.load(1L), peaked.get());
                },
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional)
             then second load (value based) should return the result of the first load
             (load cacheProvider miss followed by value cacheProvider hit)
            """)
    @Test
    void cacheMissFollowedByValueCacheHit() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        final String result2 = (String) provider.load(1L, "Number Should Come From Cache: 1", "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cacheProvider state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional) that returns null from loader
             then null should be returned and nothing cached
             (load cacheProvider miss null from loader)
            """)
    @Test
    void cacheMissFNullFromLoader() throws Exception {
        final Loader<Object, Object> firstLoader = key -> null;
        final String result = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cacheProvider state",
                () -> assertNull(result),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertTrue(provider.getKeys("NumberCache").isEmpty()));
    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional), after the TTL has elapsed
             then second load should return the result of the first load
             (load cacheProvider miss followed by cacheProvider miss, expiry on load)
            """)
    @Test
    void cacheMissFollowedByCacheHitExpiryOnLoad() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final Loader<Object, Object> secondLoader = key -> "Number Should Come From Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 30, Long.class, String.class);
        assertAll("assert initial load results and cacheProvider state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
        Thread.sleep(50L); //NOSONAR: TODO: is there a better way?
        final String result2 = (String) provider.load(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cacheProvider state",
                () -> assertEquals(secondLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional), after the TTL has elapsed
             then state interrogation reflect expiry
             (load cacheProvider miss followed by cacheProvider miss, expiry on state interrogation)
            """)
    @Test
    void cacheMissFollowedByCacheHitExpiryOnStateInterrogation() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 30, Long.class, String.class);
        assertAll("assert initial load results and cacheProvider state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
        Thread.sleep(50L); //NOSONAR: TODO: Is there a better way?
        assertAll("assert initial load results and cacheProvider state",
                () -> assertTrue(provider.getKeys("NumberCache").isEmpty()));
    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional) with incorrect key type
             then throw an exception
             (load cacheProvider miss followed by cacheProvider hit)
            """)
    @Test
    void cacheMissFollowedInvalidKeyTypeException() {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final CacheException exception = assertThrows(CacheException.class, () -> provider.load("1", firstLoader, "NumberCache", 3000, Long.class, String.class));
        assertEquals("java.lang.String is not a valid key class for cache NumberCache", exception.getMessage());
    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional) with incorrect return type
             then throw an exception
             (load cacheProvider miss followed by cacheProvider hit)
            """)
    @Test
    void cacheMissFollowedInvalidResultTypeException() {
        final Loader<Object, Object> loader = key -> Math.PI;
        final CacheException exception = assertThrows(CacheException.class, () -> provider.load(1L, loader, "NumberCache", 3000, Long.class, String.class));
        assertEquals("java.lang.Double is not a valid result class for cache NumberCache", exception.getMessage());
    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional) concurrently with a second load
             then the first load value should be loaded into the cacheProvider and returned both times
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
        assertAll("assert load results and cacheProvider state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));

    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional)
             then remove value from load
             verify that only removed key has been removed
             (load cacheProvider miss followed by remove)
            """)
    @Test
    void cacheMissFollowedByValueRemoval() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        final String result2 = (String) provider.load(2L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cacheProvider state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(2L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L, 2L), provider.getKeys("NumberCache")));
        provider.removeValue("NumberCache", 1L);
        assertAll("assert cacheProvider state after remove",
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(2L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cacheProvider
             given an initial load (non-optional)
             then clear cacheProvider
             (load cacheProvider miss followed by remove)
            """)
    @Test
    void cacheMissFollowedByPurge() throws Exception {
        final Loader<Object, Object> firstLoader = key -> "Number Loaded Into Cache: " + key;
        final String result1 = (String) provider.load(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cacheProvider state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
        provider.clearCache("NumberCache");
        assertAll("assert cacheProvider state after remove",
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            calling getProviderName should return name from implementation
            """)
    @Test
    void providerNameFromImplementation() {
       assertEquals("internal.default.cache", provider.getProviderName());
    }
}