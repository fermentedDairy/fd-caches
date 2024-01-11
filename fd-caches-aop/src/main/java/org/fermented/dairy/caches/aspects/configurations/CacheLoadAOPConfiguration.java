package org.fermented.dairy.caches.aspects.configurations;

import org.aspectj.lang.Aspects;
import org.fermented.dairy.caches.aspects.CacheLoadAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheLoadAOPConfiguration {

    @Bean
    public CacheLoadAspect getCacheLoadAspect() {
        return Aspects.aspectOf(CacheLoadAspect.class);
    }
}
