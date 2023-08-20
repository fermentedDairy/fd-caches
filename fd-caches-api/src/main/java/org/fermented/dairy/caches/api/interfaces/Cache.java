package org.fermented.dairy.caches.api.interfaces;

import java.util.Collection;
import java.util.Optional;
import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.functions.OptionalLoader;

/**
 * Cache Provider Interface.
 *
 * @noinspection rawtypes
 */
public interface Cache {

    /**
     * Load the value using the Loader function and stores it in the cache.
     * Cache Misses are:
     * <ul>
     *       <li>If the key is not present in the key list</li>
     *       <li>If the key is present, but the value is considered missing (left up to the implementation to define what that means)</li>
     *       <li> If the key is present but the value has been in the cache for longer then the TTL in ms</li>
     * </ul>
     * Cache Misses should cause the loader to be invoked. The implementation should consider the meaning of null results.
     *
     * @param key The cache key, also used as a parameter when invoking the loader function
     * @param loader The loader functional interface
     * @param cacheName The name of the cache to store the value in
     * @param ttl The time to live (ttl) before the cache expires in milliseconds
     * @param keyClass The Class object of the cache key
     * @param valueClass The Class object of the value
     *
     * @return The value stored in the cache (cache hit),
     *      the value wrapped in the Optional returned by the loader (cache miss),
     *      or an Optional if the loader returns a null
     *
     * @throws Exception Checked exception thrown by the loader function
     */
    Object load(Object key, Loader<Object, Object> loader, String cacheName, long ttl, Class keyClass, Class valueClass) throws Exception;

    /**
     * Place the value object into the named cache indexed by the provided key.
     * As this function already has a value, should only be used to force a value into a cache.
     *
     * @param key The cache key, also used as a parameter when invoking the loader function.
     * @param value The value to be placed in the cache.
     * @param cacheName The name of the cache to store the value in.
     * @param ttl The time to live (ttl) before the cache expires in milliseconds.
     * @param keyClass The Class object of the cache key.
     * @param valueClass The Class object of the value wrapped in the Optional loaded by the OptionalLoader functional interface.
     *
     * @return The value passed into this method or the value already cached.
     *
     * @throws Exception Checked exception
     */
    Object load(Object key, Object value, String cacheName, long ttl, Class keyClass, Class valueClass) throws Exception;

    /**
     * Load the value using the OptionalLoader function and stores it in the cache.
     * Expiry behaviour is the same as {@link #load(Object, Loader, String, long, Class, Class)}.
     *
     * @param key The cache key, also used as a parameter when invoking the loader function.
     * @param loader The loader functional interface, returning an Optional.
     * @param cacheName The name of the cache to store the value in.
     * @param ttl The time to live (ttl) before the cache expires in milliseconds.
     * @param keyClass The Class object of the cache key.
     * @param valueClass The Class object of the value wrapped in the Optional loaded by the OptionalLoader functional interface.
     *
     * @return An Optional containing either the previously cached value (cache hit),
     *      the value wrapped in the Optional returned by the loader (cache miss) which is then unwrapped and stored in the cache,
     *      or an Optional if the loader returns a null
     *
     * @throws Exception Checked exception thrown by the loader function.
     */
    Optional loadOptional(Object key,
                          OptionalLoader<Object, Object> loader,
                          String cacheName,
                          long ttl,
                          Class keyClass,
                          Class valueClass) throws Exception;

    /**
     * Clears all the values in all the caches.
     */
    void purge();

    /**
     * Removes the cached value associated with the key object.
     *
     * @param cacheName The cache name.
     * @param key The cache key
     */
    void removeValue(String cacheName, Object key);

    /**
     * clears all entries in the named cache.
     *
     * @param cacheName The name of the cache to clear.
     */
    void clearCache(String cacheName);

    /**
     * Gets all cache names.
     *
     * @return collection of cache names.
     */
    Collection<String> getCacheNames();

    /**
     * Gets the keys in a given cache.
     * Implementations should only include keys that are associated with values that would be considered cache hits (i.e. not expired etc.)
     *
     * @param cacheNames The name of the cache.
     *
     * @return collection of keys in the cache.
     */
    Collection<Object> getKeys(String cacheNames);

    /**
     * The provider name. should be unique within the classloader.
     *
     * @return The provider name.
     */
    default String getProviderName() {
        return getClass().getCanonicalName();
    }

}
