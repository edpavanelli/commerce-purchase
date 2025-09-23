package net.mycompany.commerce.purchase.application.exchange.service;

import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateRequestDto;
import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateResponseDto;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.valueobject.ConvertedCurrency;
import net.mycompany.commerce.purchase.domain.valueobject.TransactionId;
import net.mycompany.commerce.purchase.infrastructure.repository.PurchaseTransactionRepository;
import net.mycompany.commerce.purchase.domain.service.PurchaseDomainService;
import net.mycompany.commerce.purchase.infrastructure.config.exception.DataBaseNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CurrencyExchangeServiceTest {
    private PurchaseTransactionRepository purchaseTransactionRepository;
    private PurchaseDomainService purchaseDomainService;
    private CurrencyExchangeService service;

    @BeforeEach
    void setUp() {
        purchaseTransactionRepository = mock(PurchaseTransactionRepository.class);
        purchaseDomainService = mock(PurchaseDomainService.class);
        service = new CurrencyExchangeService(purchaseTransactionRepository, purchaseDomainService, "Not found");
    }

    @Test
    void testConvertCurrencySuccess() {
        ExchangeRateRequestDto request = ExchangeRateRequestDto.builder()
                .transactionId("tx-1")
                .countryName("Brazil")
                .build();
        Currency purchaseCurrency = Currency.builder().code("BRL").name("Real").country("Brazil").build();
        PurchaseTransaction tx = new PurchaseTransaction(
                new TransactionId("tx-1"),
                new BigDecimal("10.00"),
                purchaseCurrency,
                LocalDate.now(),
                "desc");
        ConvertedCurrency converted = ConvertedCurrency.builder()
                .currency(purchaseCurrency)
                .exchangeRateAmount(new BigDecimal("2.00"))
                .convertedAmount(new BigDecimal("20.00"))
                .build();
        when(purchaseTransactionRepository.findByTransactionId(any())).thenReturn(Optional.of(tx));
        when(purchaseDomainService.currencyConversion(any(), any())).thenReturn(converted);
        ExchangeRateResponseDto response = service.convertCurrency(request);
        assertEquals("tx-1", response.getTransactionId());
        assertEquals(new BigDecimal("20.00"), response.getTargetAmount());
        assertEquals(new BigDecimal("2.00"), response.getExchangeRate());
    }

    @Test
    void testConvertCurrencyThrowsNotFound() {
        ExchangeRateRequestDto request = ExchangeRateRequestDto.builder().transactionId("tx-1").countryName("Brazil").build();
        when(purchaseTransactionRepository.findByTransactionId(any())).thenReturn(Optional.empty());
        assertThrows(DataBaseNotFoundException.class, () -> service.convertCurrency(request));
    }
}
