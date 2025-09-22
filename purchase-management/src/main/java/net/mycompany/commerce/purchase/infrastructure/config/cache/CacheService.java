package net.mycompany.commerce.purchase.infrastructure.config.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchase.domain.valueobject.ExchangeRate;


@Component
public class CacheService {
	
	private static final Logger log = LoggerFactory.getLogger(CacheService.class);
	
	private final CacheManager cacheManager;
	
	public CacheService(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	
	
    public void cacheExchangeRate(String country, List<ExchangeRate> exchangeRateList) {	
		
		cacheManager.getCache("treasuryExchangeRateCache").put(country, exchangeRateList);
		
        log.info("Cached exchange rate for {}: ExchangeRate={}", country, exchangeRateList.toArray());
        
    }

	
	public List<ExchangeRate> getCachedExchangeRateList(String country) {
		Cache cache = cacheManager.getCache("treasuryExchangeRateCache");
	    if (cache != null) {
	        Cache.ValueWrapper wrapper = cache.get(country);
	        if (wrapper != null) {
	            return (List<ExchangeRate>) wrapper.get();
	        }
	    }
	    return null;
        
    }

}
