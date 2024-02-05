package org.fermented.dairy.caches.sb.aop.rest;

import org.fermented.dairy.caches.aspects.CacheConfigurations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import(CacheConfigurations.class)
public class CacheDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CacheDemoApplication.class, args);
    }

}
