package org.fermented.dairy.caches.sb.aop.rest.boundary;

import exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.fermented.dairy.caches.api.interfaces.CacheProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import  io.swagger.v3.oas.annotations.responses.ApiResponse;
import  io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("caches")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CacheAdminRestController {
    private List<CacheProvider> caches;

    /**
     * Get all cache provider names.
     *
     * @return provider names
     */
    @GetMapping("providers")
    @ApiResponse(
            responseCode = "200",
            description = "A set of provider names",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of provider names")
    public Set<String> getProviders() {
        return caches.stream().map(CacheProvider::getProviderName).collect(Collectors.toSet());
    }

    /**
     * Get all cache provider names.
     *
     * @return provider names
     */
    @DeleteMapping("providers")
    @ApiResponse(
            responseCode = "200",
            description = "purge providers",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
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
    @GetMapping("providers/{provider}")
    @ApiResponse(
            responseCode = "200",
            description = "A set of cache names in a provider",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(
            responseCode = "404",
            description = "If the provider does not exist",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of provider names")
    public Set<String> getCacheNames(@PathVariable("provider") @NotNull final String provider) {
        return Set.copyOf(caches.stream()
                .filter(cache -> provider.equalsIgnoreCase(cache.getProviderName()))
                .findFirst().map(CacheProvider::getCacheNames)
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
    @GetMapping("providers/{provider}/caches/{cacheName}/keys")
    @ApiResponse(
            responseCode = "404",
            description = "If the provider or cache does not exist",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(
            responseCode = "200",
            description = "A set of the cache keys in the cache",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    public Set<Object> getKeys(@PathVariable("provider") @NotNull final String provider,
                               @PathVariable("cacheName") @NotNull final String cacheName) {
        final CacheProvider cacheProvider = caches.stream()
                .filter(cacheItem -> provider.equalsIgnoreCase(cacheItem.getProviderName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("cache provider named %s not found".formatted(provider)
                ));

        if (!cacheProvider.getCacheNames().contains(cacheName)) {
            throw new NotFoundException(
                    "Cache provider named %s does not contain a cache named %s".formatted(provider, cacheName));
        }
        return Set.copyOf(cacheProvider.getKeys(cacheName));
    }

    /**
     * peek all object in cache.
     *
     * @param provider the name of the provider
     * @param cacheName the name of the cache
     * @param key the cache key
     * @return cacheKeys
     */
    @GetMapping("providers/{provider}/caches/{cacheName}/keys/{key}")
    @ApiResponse(
            responseCode = "404",
            description = "If the provider, cache or value does not exist",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(
            responseCode = "200",
            description = "The cached value",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    public Object getObject(@PathVariable("provider") @NotNull final String provider,
                            @PathVariable("cacheName") @NotNull final String cacheName,
                            @PathVariable("key") @NotNull final UUID key) {

        final CacheProvider cacheProvider = caches.stream()
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
