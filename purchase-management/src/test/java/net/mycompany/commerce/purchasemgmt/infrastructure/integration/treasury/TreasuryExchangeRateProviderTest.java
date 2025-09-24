package net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

import net.mycompany.commerce.purchasemgmt.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.cache.CacheService;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.rest.TreasuryApiProperties;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.rest.WebClientFactory;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.TreasuryExchangeRateProvider;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.TreasuryExchangeRateSortDto;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.mapper.ExchangeRateMapper;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TreasuryExchangeRateProviderTest {
    private Environment env;
    private WebClientFactory webClientFactory;
    private TreasuryApiProperties properties;
    private ExchangeRateMapper exchangeRateMapper;
    private CacheService cacheService;
    private WebClient webClient;
    private TreasuryExchangeRateProvider provider;
    private ReactiveCircuitBreaker treasuryBreaker;
    private ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @BeforeEach
    void setUp() {
        env = mock(Environment.class);
        webClientFactory = mock(WebClientFactory.class);
        properties = mock(TreasuryApiProperties.class);
        exchangeRateMapper = mock(ExchangeRateMapper.class);
        cacheService = mock(CacheService.class);
        webClient = mock(WebClient.class);
        treasuryBreaker = mock(ReactiveCircuitBreaker.class);
        circuitBreakerFactory = mock(ReactiveCircuitBreakerFactory.class);
		when(circuitBreakerFactory.create(anyString())).thenReturn(treasuryBreaker);
        when(webClientFactory.createJsonWebClient(any())).thenReturn(webClient);
        provider = new TreasuryExchangeRateProvider(env, webClientFactory, properties, exchangeRateMapper, cacheService, circuitBreakerFactory);
    }

    @Test
    void testGetTreasuryExchangeRateFromRestClientSuccess() {
        TreasuryExchangeRateFilterDto filter = TreasuryExchangeRateFilterDto.builder()
                .country("Brazil")
                .requestDateTo(LocalDate.now())
                .requestDateFrom(LocalDate.now().minusMonths(6))
                .sortBy(TreasuryExchangeRateSortDto.EFFECTIVE_DATE)
                .build();
        List<ExchangeRate> expectedRates = Arrays.asList(mock(ExchangeRate.class));
        TreasuryExchangeRateProvider providerSpy = Mockito.spy(provider);
        doReturn(Mono.just(expectedRates)).when(providerSpy).getTreasuryExchangeRateFromRestClient(any());
        Mono<List<ExchangeRate>> result = providerSpy.getTreasuryExchangeRateFromRestClient(filter);
        assertEquals(expectedRates, result.block());
    }

    @Test
    void testCacheExchangeRatesOnApplicationReadySuccess() {
        when(env.getProperty("environment.default.cuntries.exchange")).thenReturn("Brazil,Argentina");
        TreasuryExchangeRateProvider providerSpy = Mockito.spy(provider);
        doReturn(Mono.just(Arrays.asList(mock(ExchangeRate.class)))).when(providerSpy).getTreasuryExchangeRateFromRestClient(any());
        providerSpy.cacheExchangeRatesOnApplicationReady();
        verify(cacheService, atLeastOnce()).cacheExchangeRate(any(), any());
    }

    @Test
    void testCacheExchangeRatesOnApplicationReadyException() {
        when(env.getProperty("environment.default.cuntries.exchange")).thenReturn("Brazil");
        TreasuryExchangeRateProvider providerSpy = Mockito.spy(provider);
        doReturn(Mono.error(new RuntimeException("API error"))).when(providerSpy).getTreasuryExchangeRateFromRestClient(any());
        providerSpy.cacheExchangeRatesOnApplicationReady();
        verify(cacheService, never()).cacheExchangeRate(any(), any());
    }
}