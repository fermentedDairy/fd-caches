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
import org.fermented.dairy.caches.api.functions.OptionalLoader;
import org.fermented.dairy.caches.api.interfaces.Cache;
import org.fermented.dairy.caches.providers.HashMapCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HashMapCacheProviderOptionalLoaderTest {

    Cache provider = new HashMapCache();

    @BeforeEach
    void beforeEach() {
        provider.purge();
        assertTrue(provider.getCacheNames().isEmpty());
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (optional)\s
            then second load should return the result of the first load\s
            (load cache miss followed by cache hit)
            """)
    @Test
    void cacheMissFollowedByCacheHit() throws Exception {
        final OptionalLoader<Object, Object> firstLoader = key -> Optional.of(
                "Number Loaded Into Cache: " + key);
        final OptionalLoader<Object, Object> secondLoader = key -> Optional.of(
                "Number Should Come From Cache: " + key);
        final Optional result1 = provider.loadOptional(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        final Optional result2 = provider.loadOptional(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (optional)\s
            then second load (non-optional) should return the result of the first load\s
            (load cache miss followed by cache hit (common cache name)
            """)
    @Test
    void cacheMissFollowedByCacheHitNonOptional() throws Exception {
        final OptionalLoader<Object, Object> firstLoader = key -> Optional.of(
                "Number Loaded Into Cache: " + key);
        final Loader<Object, Object> secondLoader = key -> "Number Should Come From Cache: " + key;
        final Optional result1 = provider.loadOptional(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        final String result2 = (String) provider.load(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(1L).get(), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (optional) that returns empty from loader\s
            then empty should be returned and nothing cached\s
            (load cache miss null from loader)
            """)
    @Test
    void cacheMissOptionalFromLoader() throws Exception {
        final OptionalLoader<Object, Object> firstLoader = key -> Optional.empty();
        final Optional result = provider.loadOptional(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertTrue(result.isEmpty()),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertTrue(provider.getKeys("NumberCache").isEmpty()));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (optional) that returns null from loader\s
            then empty Optional should be returned and nothing cached\s
            (load cache miss null from loader)
            """)
    @Test
    void cacheMissNullFromLoader() throws Exception {
        final OptionalLoader<Object, Object> firstLoader = key -> null;
        final Optional result = provider.loadOptional(1L, firstLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertTrue(result.isEmpty()),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertTrue(provider.getKeys("NumberCache").isEmpty()));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (optional), after the TTL has elapsed\s
            then second load should return the result of the first load\s
            (load cache miss followed by cache miss, expiry on load)
            """)
    @Test
    void cacheMissFollowedByCacheHitExpiryOnLoad() throws Exception {
        final OptionalLoader<Object, Object> firstLoader = key ->
                Optional.of("Number Loaded Into Cache: " + key);
        final OptionalLoader<Object, Object> secondLoader = key ->
                Optional.of("Number Should Come From Cache: " + key);
        final Optional result1 = provider.loadOptional(1L, firstLoader, "NumberCache", 30, Long.class, String.class);
        assertAll("assert initial load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
        Thread.sleep(50L); //NOSONAR: TODO: is there a better way?
        final Optional result2 = provider.loadOptional(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        assertAll("assert load results and cache state",
                () -> assertEquals(secondLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (optional), after the TTL has elapsed\s
            then state interrogation reflect expiry\s
            (load cache miss followed by cache miss, expiry on state interrogation)
            """)
    @Test
    void cacheMissFollowedByCacheHitExpiryOnStateInterrogation() throws Exception {
        final OptionalLoader<Object, Object> firstLoader = key -> Optional.of(
                "Number Loaded Into Cache: " + key);
        final Optional result1 = provider.loadOptional(1L, firstLoader, "NumberCache", 30, Long.class, String.class);
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
            given an initial load (optional) with incorrect key type\s
            then throw an exception\s
            (load cache miss followed by cache hit)
            """)
    @Test
    void cacheMissFollowedInvalidKeyTypeException() {
        final OptionalLoader<Object, Object> firstLoader = key -> Optional.of(
                "Number Loaded Into Cache: " + key);
        final CacheException exception = assertThrows(CacheException.class, () -> provider.loadOptional("1", firstLoader, "NumberCache", 3000, Long.class, String.class));
        assertEquals("java.lang.String is not a valid key class for cache NumberCache", exception.getMessage());
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (optional) with incorrect return type\s
            then throw an exception\s
            (load cache miss followed by cache hit)
            """)
    @Test
    void cacheMissFollowedInvalidResultTypeException() {
        final OptionalLoader<Object, Object> loader = key -> Optional.of(Math.PI);
        final CacheException exception = assertThrows(CacheException.class, () -> provider.loadOptional(1L, loader, "NumberCache", 3000, Long.class, String.class));
        assertEquals("java.lang.Double is not a valid result class for cache NumberCache", exception.getMessage());
    }

    @DisplayName("""
            with an empty cache\s
            given an initial load (optional) concurrently with a second load\s
            then the first load value should be loaded into the cache and returned both times\s
            (concurrent load with same key thread locking with same key)
            """)
    @Test
    void concurrentLoadWithSameKeyBlocksAllThreads() throws Exception {
        final OptionalLoader<Object, Object> firstLoader = key -> {
            Thread.sleep(1000L); //NOSONAR: java:S2925 simulating latency in load function
            return Optional.of("Number Loaded Into Cache: " + key);
        };
        final OptionalLoader<Object, Object> secondLoader = key -> Optional.of(
                "Number Should Come From Cache: " + key);

        final Future<Optional> futureResult1 = Executors.newSingleThreadExecutor().submit(
                () -> provider.loadOptional(1L, firstLoader, "NumberCache", 3000, Long.class, String.class));
        Thread.sleep(50L); ///NOSONAR: TODO: Is there a better way?
        final Optional result2 = provider.loadOptional(1L, secondLoader, "NumberCache", 3000, Long.class, String.class);
        final Optional result1 = futureResult1.get();
        assertAll("assert load results and cache state",
                () -> assertEquals(firstLoader.load(1L), result1),
                () -> assertEquals(firstLoader.load(1L), result2),
                () -> assertEquals(Set.of("NumberCache"), provider.getCacheNames()),
                () -> assertEquals(Set.of(1L), provider.getKeys("NumberCache")));

    }
}