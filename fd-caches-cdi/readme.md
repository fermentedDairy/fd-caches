# fd-caches-cdi

## Overview
A CDI interceptor driven caching implementation.

## Preparing Data Classes For Caching

Classes and records that will be cached need to be annotated with the [@Cached](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/Cached.java) annotation. 
A field must also be annotated with the [@CacheKey](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/CacheKey.java) when deleting based on the cached object.
For Example:
```java
@Cached
public record DefaultCacheRecord(@CacheKey UUID id, String value) {
}
```

## Inserting Objects Into Caches

Methods that are annotated with the [@CacheLoad](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/CacheLoad.java) annotation will:
- **On cache miss** Call the annotated function and insert the result into the cache.
- **On cache hit** Return the previously cached result. The annotated method is not called, it is discouraged to include any side effects into these functions. 

Some examples:

If there is a single parameter it is used as the cache key.
~~~java
@CacheLoad
public DefaultCacheEntityClass defaultLoad(final Long param){
    return new DefaultCacheEntityClass(param);
}
~~~

If there are multiple parameters, the parameter annotated with [@CacheKey](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/CacheKey.java)
is used as teh cache key. If this annotation is missing an exception is thrown.
~~~java
@CacheLoad
public DefaultCacheEntityClass defaultLoad(final Object ignoredDummy, @CacheKey final Long param) {
    return new DefaultCacheEntityClass(param);
}
~~~
If the return type is `Optional` then the [@CachedType](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/CachedType.java) 
is required.
~~~java
@CacheLoad
@CachedType(DefaultCacheEntityClass.class)
public Optional<DefaultCacheEntityClass> defaultOptionalLoad(final Long param){
    return Optional.of(new DefaultCacheEntityClass(param));
}
~~~

## Deleting Objects From Caches

Methods that are annotated with the [@CacheDelete](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/CacheDelete.java)
annotation will remove the value from the cache after executing the method.

Some examples:

If the method has a single parameter, it is used as the cache key.
~~~java
@CacheDelete
@CachedType(DefaultCacheEntityClass.class)
public DefaultCacheEntityClass deleteDefault(Long key) {
    return null;
}
~~~

If there are multiple parameters, then the parameter annotated with [@CacheKey](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/CacheKey.java)
is used as teh cache key. If the annotation is missing, an exception is thrown.
~~~java
@CacheDelete
@CachedType(DefaultCacheEntityClass.class)
public DefaultCacheEntityClass deleteDefault(Object ignoredDummy, @CacheKey Long key) {
    return null;
}
~~~

If the only parameter is an instance of the cached class then the field annotated with [@CacheKey](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/CacheKey.java)
is used.
~~~java
@CacheDelete
public DefaultCacheEntityClass deleteDefault(DefaultCacheEntityClass toDelete) {
    return null;
}
~~~

## The Cache Provider
### Creating A Custom Cache Provider
Creating a custom creator is recommended. 
This is done by creating a CDI managed bean implementing the [CacheProvider](../fd-caches-api/src/main/java/org/fermented/dairy/caches/api/interfaces/CacheProvider.java) interface.

### Defining Usage Using Annotation
Once a provider has been created, a bean can be cached in it by using the [@Cached](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/Cached.java) annotation.
In this example, the cache provider name is being set to `namedCache`:
```java
@Cached(cacheProviderName = "namedCache")
public record NamedCacheRecord(@CacheKey UUID id, String value) {
}
```
### Overriding Using Config
Cache providers can be overridden at runtime using the properties file. 
The provider name can be set by setting the `fd.config.cache.<canonical cached record name>.cacheprovider` property where `canonical cached record name>.cacheprovider`
if the value returned by `Class.getCanonicalName()`.

### Defining Default Provider
The default provider can be configured using the `fd.config.cache.provider.default`. It must match the result of one and only one provider's `getProviderName()` method.
If not present `internal.default.cache` is used. A sample default can be found by examining the [HashMapCacheProvider](../fd-caches-providers/src/main/java/org/fermented/dairy/caches/providers/HashMapCacheProvider.java).

## TTL (Time To Live)
The TTL or time to expiry defines the period where a cache entry is alive and after how long an entry can be evicted from the cache.
The TTL is measured in ms. 

### Defining Usage Using Annotation
The TTL can be set statically by using the [@Cached](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/Cached.java) annotation. 
In this example, the TTL is being set to 500 ms:
```java
@Cached(ttlMilliSeconds = 500L)
public record NamedCacheRecord(@CacheKey UUID id, String value) {
}
```

### Overriding Using Config
The TTL can be set at runtime by setting the `fd.config.cache.<canonical cached record name>.ttlms` configuration. If not set the default is 3600000 ms or 1 hour.

## Cache Name
Cache providers can cache multiple different types of records without risking key collisions by partitioning the caches by name.

### Defining Usage Using Annotation
The cache name can be defined statically by using the [@Cached](../fd-caches-annotations/src/main/java/org/fermented/dairy/caches/annotations/Cached.java) annotation.
If not present, the default is the canonical classname of the cached record. In this example, the cache name is being set to `namedCacheRecords`:
```java
@Cached(cacheName = "namedCacheRecords")
public record NamedCacheRecord( @CacheKey UUID id, String value) {
}
```

### Overriding Using Config
The cache name can be set at runtime using the `fd.config.cache.<canonical cached record name>.cachename` configuration.

## Default Cache Provider
### Overriding Usage Using Config
The default cache provider can be set at runtime using the `fd.config.cache.provider.default`.

## Disabling caching for a record
The caching for a specific record can be disabled at runtime by setting the configuration `fd.config.cache.<canonical cached record name>.disabled` to `true`.
Caches are enabled by default.

## Configuration

The following configuration options are available and described in more detail above:

```
fd.config.cache.ttl.default=<default ttl (1 hour if not configured)>
fd.config.cache.provider.default=<default provider name (internal.default.cache if not present)>
fd.config.cache.<canonical cached record name>.cacheprovider=<cache Provider Name> 
fd.config.cache.<canonical cached record name>.cachename=<Cache name>
fd.config.cache.<canonical cached record name>.ttlms=<TTL in ms>
fd.config.cache.<canonical cached record name>.disabled=<true if cache should be disabled>
```