package net.mycompany.commerce.purchase.infrastructure.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Cria um cache em memória simples
        return new ConcurrentMapCacheManager("treasuryExchangeRateCache");
    }
}
