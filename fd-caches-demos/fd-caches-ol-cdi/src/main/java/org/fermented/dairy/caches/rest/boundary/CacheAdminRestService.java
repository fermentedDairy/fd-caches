package org.fermented.dairy.caches.rest.boundary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.fermented.dairy.caches.api.interfaces.Cache;

/**
 * REST boundary for cache admin.
 */
@ApplicationScoped
@AllArgsConstructor(onConstructor = @__(@Inject))
@NoArgsConstructor
@Path("/caches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CacheAdminRestService {

    private Instance<Cache> caches;

    /**
     * Get all cache provider names.
     *
     * @return provider names
     */
    @GET
    @Path("providers")
    @APIResponse(
            responseCode = "200",
            description = "A set of provider names",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of provider names")
    public Set<String> getProviders() {
        return caches.stream().map(Cache::getProviderName).collect(Collectors.toSet());
    }

    /**
     * Get all cache provider names.
     *
     * @return provider names
     */
    @DELETE
    @Path("providers")
    @APIResponse(
            responseCode = "200",
            description = "purge providers",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of provider names")
    public Map<String, String> purgeProviders() {
        return caches.stream().map(cache -> {
            cache.purge();
            return cache.getProviderName();
        }).collect(Collectors.toMap(
                Function.identity(),
                str -> "Purged"
        ));
    }

    /**
     * Get all cache names in a provider.
     *
     * @param provider the name of the provider
     * @return provider names
     */
    @GET
    @Path("providers/{provider}")
    @APIResponse(
            responseCode = "200",
            description = "A set of cache names in a provider",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(
            responseCode = "404",
            description = "If the provider does not exist",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of provider names")
    public Set<String> getCacheNames(@PathParam("provider") @NotNull final String provider) {
        return Set.copyOf(caches.stream()
                .filter(cache -> provider.equalsIgnoreCase(cache.getProviderName()))
                .findFirst().map(Cache::getCacheNames)
                .orElseThrow(() -> new NotFoundException("cache provider named %s not found".formatted(provider)
                )));
    }

    /**
     * Get all keys for the cache in the provider.
     *
     * @param provider the name of the provider
     * @param cacheName the name of the cache
     *
     * @return cacheKeys
     */
    @GET
    @Path("providers/{provider}/caches/{cacheName}/keys")
    @APIResponse(
            responseCode = "404",
            description = "If the provider or cache does not exist",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(
            responseCode = "200",
            description = "A set of the cache keys in the cache",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    public Set<Object> getKeys(@PathParam("provider") @NotNull final String provider,
                               @PathParam("cacheName") @NotNull final String cacheName) {
        final Cache cache = caches.stream()
                .filter(cacheItem -> provider.equalsIgnoreCase(cacheItem.getProviderName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("cache provider named %s not found".formatted(provider)
                ));

        if (!cache.getCacheNames().contains(cacheName)) {
            throw new NotFoundException(
                    "Cache provider named %s does not contain a cache named %s".formatted(provider, cacheName));
        }
        return Set.copyOf(cache.getKeys(cacheName));
    }

    /**
     * peek all object in cache.
     *
     * @param provider the name of the provider
     * @param cacheName the name of the cache
     * @param key the cache key
     * @return cacheKeys
     */
    @GET
    @Path("providers/{provider}/caches/{cacheName}/keys/{key}")
    @APIResponse(
            responseCode = "404",
            description = "If the provider, cache or value does not exist",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(
            responseCode = "200",
            description = "The cached value",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    public Object getObject(@PathParam("provider") @NotNull final String provider,
                          @PathParam("cacheName") @NotNull final String cacheName,
                          @PathParam("key") @NotNull final UUID key) {

        final Cache cacheProvider = caches.stream()
                .filter(cache -> provider.equalsIgnoreCase(cache.getProviderName()))
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException("cache provider named %s not found".formatted(provider))
                );
        return cacheProvider.peek(cacheName, key)
                .orElseThrow(
                        () -> new NotFoundException(
                                "No value found in cache %s for key %s".formatted(cacheName, key)
                        )
                );
    }
}
