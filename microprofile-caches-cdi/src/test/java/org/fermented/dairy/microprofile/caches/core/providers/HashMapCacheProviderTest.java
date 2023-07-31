package org.fermented.dairy.microprofile.caches.core.providers;

import org.fermented.dairy.microprofile.caches.api.interfaces.CacheProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

class HashMapCacheProviderTest {

    CacheProvider provider = new HashMapCacheProvider();

    @BeforeEach
    void beforeEach(){
        provider.getCacheNames().forEach(provider::clearCache);
        Assertions.assertTrue(provider.getCacheNames().isEmpty());
    }

}