# fd-caches-api

A Simple API for implementing generic name based caches.

## Overview
A cacheProvider provider provides access to a set of named caches. Each cacheProvider is a key value pair. 
Only time based TTL expiry eviction is guaranteed, although implementations can internally implement other cacheProvider wide eviction policies in conjunction with the TTL expiry eviction strategy.