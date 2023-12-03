# fd-caches-api

A Simple API for implementing generic name based caches.

## Overview
A cacheProvider provider provides access to a set of named caches. Each cacheProvider is a key value pair. 
Only time based TTL expiry eviction is guaranteed, although implementations can internally implement other cacheProvider wide eviction policies in conjunction with the TTL expiry eviction strategy.

## Named Cache Scheme

Cache are all named, each named cache then contains a key-value pair of cache keys and values.
So if there are 3 caches each with 3 key value pairs each then the cache would logically be laid out as: \
```
| provider 
|   |-> cache1 
|   |   -> key1 -> value1 
|   |   -> key2 -> value2 
|   |   -> key3 -> value3 
|
|   |-> cache2 
|   |   ->  key4 -> value4 
|   |   ->  key5 -> value5 
|   |   ->  key6 -> value6 
|
|   |-> cache3 
|   |   ->  key7 -> value7 
|   |   ->  key8 -> value8 
|   |   ->  key9 -> value9 
```
To get the value associated with key4 for example, the caching implementation will need to search for and navigate
to the cache named `cache2` and then search for `key4`.  