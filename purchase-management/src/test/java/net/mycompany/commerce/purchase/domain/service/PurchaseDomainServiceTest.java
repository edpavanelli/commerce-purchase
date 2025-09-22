package net.mycompany.commerce.purchase.domain.service;

import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.domain.port.ExchangeRateProviderPort;
import net.mycompany.commerce.purchase.domain.valueobject.ConvertedCurrency;
import net.mycompany.commerce.purchase.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchase.infrastructure.config.cache.CacheService;
import net.mycompany.commerce.purchase.infrastructure.config.exception.ApiServiceUnavaliableException;
import net.mycompany.commerce.purchase.infrastructure.config.exception.PurchaseDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurchaseDomainServiceTest {
    private ExchangeRateProviderPort providerPort;
    private CacheService cacheService;
    private PurchaseDomainService service;

    @BeforeEach
    void setUp() {
        providerPort = mock(ExchangeRateProviderPort.class);
        cacheService = mock(CacheService.class);
        service = new PurchaseDomainService(
                providerPort,
                cacheService,
                "ERR1", "Not last 6 months",
                "Treasury error",
                "ERR2", "Null param error"
        );
    }

    @Test
    void testCurrencyConversionSuccessFromCache() {
        Currency currency = Currency.builder().country("Brazil").build();
        PurchaseTransaction tx = PurchaseTransaction.builder()
                .amount(new BigDecimal("10.00"))
                .currency(currency)
                .purchaseDate(LocalDate.now())
                .build();
        ExchangeRate rate = ExchangeRate.builder().exchangeRateAmount(new BigDecimal("2.00")).effectiveDate(LocalDate.now()).build();
        when(cacheService.getCachedExchangeRateList(any())).thenReturn(List.of(rate));
        ConvertedCurrency result = service.currencyConversion(tx, currency);
        assertEquals(new BigDecimal("20.00"), result.getConvertedAmount());
    }

    @Test
    void testCurrencyConversionSuccessFromProvider() {
        Currency currency = Currency.builder().country("Brazil").build();
        PurchaseTransaction tx = PurchaseTransaction.builder()
                .amount(new BigDecimal("10.00"))
                .currency(currency)
                .purchaseDate(LocalDate.now().minusDays(1))
                .build();
        ExchangeRate rate = ExchangeRate.builder().exchangeRateAmount(new BigDecimal("2.00")).effectiveDate(LocalDate.now().minusDays(1)).build();
        when(cacheService.getCachedExchangeRateList(any())).thenReturn(null);
        when(providerPort.getTreasuryExchangeRateFromRestClient(any())).thenReturn(Mono.just(List.of(rate)));
        ConvertedCurrency result = service.currencyConversion(tx, currency);
        assertEquals(new BigDecimal("20.00"), result.getConvertedAmount());
    }

    @Test
    void testCurrencyConversionThrowsForNullPurchaseTransaction() {
        Currency currency = Currency.builder().country("Brazil").build();
        assertThrows(PurchaseDomainException.class, () -> service.currencyConversion(null, currency));
    }

    @Test
    void testCurrencyConversionThrowsForNullCurrency() {
        PurchaseTransaction tx = PurchaseTransaction.builder().amount(new BigDecimal("10.00")).purchaseDate(LocalDate.now()).build();
        assertThrows(PurchaseDomainException.class, () -> service.currencyConversion(tx, null));
    }

    @Test
    void testCurrencyConversionThrowsForNoRates() {
        Currency currency = Currency.builder().country("Brazil").build();
        PurchaseTransaction tx = PurchaseTransaction.builder().amount(new BigDecimal("10.00")).currency(currency).purchaseDate(LocalDate.now()).build();
        when(cacheService.getCachedExchangeRateList(any())).thenReturn(null);
        when(providerPort.getTreasuryExchangeRateFromRestClient(any())).thenReturn(Mono.just(Collections.emptyList()));
        assertThrows(ApiServiceUnavaliableException.class, () -> service.currencyConversion(tx, currency));
    }

    @Test
    void testCurrencyConversionThrowsForProviderException() {
        Currency currency = Currency.builder().country("Brazil").build();
        PurchaseTransaction tx = PurchaseTransaction.builder().amount(new BigDecimal("10.00")).currency(currency).purchaseDate(LocalDate.now()).build();
        when(cacheService.getCachedExchangeRateList(any())).thenReturn(null);
        when(providerPort.getTreasuryExchangeRateFromRestClient(any())).thenReturn(Mono.error(new RuntimeException("API error")));
        assertThrows(ApiServiceUnavaliableException.class, () -> service.currencyConversion(tx, currency));
    }

    @Test
    void testCalculateCurrencyConversionSuccess() {
        BigDecimal result = service.calculateCurrencyConversion(new BigDecimal("10.00"), new BigDecimal("2.00"));
        assertEquals(new BigDecimal("20.00"), result);
    }

    @Test
    void testCalculateCurrencyConversionThrowsForNullAmount() {
        assertThrows(PurchaseDomainException.class, () -> service.calculateCurrencyConversion(null, new BigDecimal("2.00")));
    }

    @Test
    void testCalculateCurrencyConversionThrowsForNullRate() {
        assertThrows(PurchaseDomainException.class, () -> service.calculateCurrencyConversion(new BigDecimal("10.00"), null));
    }
}