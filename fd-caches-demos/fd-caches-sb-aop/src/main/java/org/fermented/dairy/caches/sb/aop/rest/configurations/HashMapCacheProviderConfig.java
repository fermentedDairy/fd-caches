package org.fermented.dairy.caches.sb.aop.rest.configurations;

import org.fermented.dairy.caches.providers.HashMapCacheProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HashMapCacheProviderConfig {

    @Bean
    HashMapCacheProvider getHashMapCacheProvider() {
        return new HashMapCacheProvider();
    }
}
