# fd-caches-cdi

## Overview
A CDI interceptor driven caching implementation.

## Preparing Classes For Caching

Classes and records that will be cached, need to be annotated with the [@Cached](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/Cached.java) annotation. 
A field must also be annotated with the [@CacheKey](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/CacheKey.java) when deleting based on the cached object.

## Inserting Objects Into Caches

## Deleting Objects From Caches

## The Cache Provider
### Building A Custom Provider
### Defining Usage Using Annotation
### Overriding Using Config

## TTL
### Defining Usage Using Annotation
### Overriding Using Config

## Default Cache Provider
### Overriding Usage Using Config

## Configuration

The following configuration options are available

```
fd.config.cache.ttl.default=<default ttl (1 hour if not configured)>
fd.config.cache.provider.default=<default provider name (internal.default.cache if not present)>
fd.config.cache.<canonical cached record name>.cacheprovider=<cache Provider Name> 
fd.config.cache.<canonical cached record name>.cachename=<Cache name>
fd.config.cache.<canonical cached record name>.ttlms=<TTL in ms>
fd.config.cache.<canonical cached record name>.disabled=<true if cache should be disabled>
```