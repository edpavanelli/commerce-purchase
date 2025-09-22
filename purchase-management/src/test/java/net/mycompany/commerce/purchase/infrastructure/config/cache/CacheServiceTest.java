package net.mycompany.commerce.purchase.infrastructure.config.cache;

import net.mycompany.commerce.purchase.domain.valueobject.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CacheServiceTest {
    private CacheManager cacheManager;
    private Cache cache;
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);
        when(cacheManager.getCache("treasuryExchangeRateCache")).thenReturn(cache);
        cacheService = new CacheService(cacheManager);
    }

    @Test
    void testCacheExchangeRateStoresList() {
        List<ExchangeRate> rates = Arrays.asList(mock(ExchangeRate.class));
        cacheService.cacheExchangeRate("Brazil", rates);
        verify(cache, times(1)).put("Brazil", rates);
    }

    @Test
    void testGetCachedExchangeRateListReturnsList() {
        List<ExchangeRate> rates = Arrays.asList(mock(ExchangeRate.class));
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(cache.get("Brazil")).thenReturn(wrapper);
        when(wrapper.get()).thenReturn(rates);
        List<ExchangeRate> result = cacheService.getCachedExchangeRateList("Brazil");
        assertEquals(rates, result);
    }

    @Test
    void testGetCachedExchangeRateListReturnsNullIfNoCache() {
        when(cacheManager.getCache("treasuryExchangeRateCache")).thenReturn(null);
        List<ExchangeRate> result = cacheService.getCachedExchangeRateList("Brazil");
        assertNull(result);
    }

    @Test
    void testGetCachedExchangeRateListReturnsNullIfNoValue() {
        when(cache.get("Brazil")).thenReturn(null);
        List<ExchangeRate> result = cacheService.getCachedExchangeRateList("Brazil");
        assertNull(result);
    }
}
