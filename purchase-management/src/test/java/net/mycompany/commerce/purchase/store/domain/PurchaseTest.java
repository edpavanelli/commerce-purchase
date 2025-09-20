package net.mycompany.commerce.purchase.store.domain;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponse;
import net.mycompany.commerce.purchase.application.store.mapper.PurchaseTransactionMapper;
import net.mycompany.commerce.purchase.application.store.service.StorePurchaseService;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.infrastructure.config.audit.PurchaseTransactionSubject;
import net.mycompany.commerce.purchase.infrastructure.config.audit.TransactionObserver;
import net.mycompany.commerce.purchase.infrastructure.config.exception.DataBaseNotFoundException;
import net.mycompany.commerce.purchase.infrastructure.repository.CurrencyRepository;
import net.mycompany.commerce.purchase.infrastructure.repository.PurchaseTransactionRepository;
import net.mycompany.commerce.purchase.domain.model.port.TransactionIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurchaseTest {
    private CurrencyRepository currencyRepository;
    private PurchaseTransactionRepository purchaseTransactionRepository;
    private PurchaseTransactionSubject purchaseTransactionSubject;
    private TransactionObserver transactionObserver;
    private StorePurchaseService purchase;
    private PurchaseTransactionMapper purchaseTransactionMapper;
    private TransactionIdGenerator transactionIdGenerator;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        purchaseTransactionRepository = mock(PurchaseTransactionRepository.class);
        purchaseTransactionSubject = mock(PurchaseTransactionSubject.class);
        transactionObserver = mock(TransactionObserver.class);
        purchaseTransactionMapper = mock(PurchaseTransactionMapper.class);
        transactionIdGenerator = mock(TransactionIdGenerator.class);
        purchase = new StorePurchaseService(
            purchaseTransactionRepository,
            currencyRepository,
            "USD",
            purchaseTransactionSubject,
            transactionObserver,
            purchaseTransactionMapper,
            transactionIdGenerator
        );
    }

    @Test
    void testStorePurchaseSuccess() {
        StorePurchaseRequest request = StorePurchaseRequest.builder()
            .amount(new BigDecimal("100.00"))
            .description("Test purchase")
            .purchaseDate(LocalDateTime.now())
            .build();
        Currency currency = Currency.builder()
            .code("USD")
            .name("US Dollar")
            .country("United States")
            .build();
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currency));
        when(transactionIdGenerator.nextId()).thenReturn("tx-123");
        // Mock the mapper to return a domain object with setters
        var purchaseTransaction = mock(net.mycompany.commerce.purchase.domain.model.PurchaseTransaction.class);
        when(purchaseTransactionMapper.toDomain(request)).thenReturn(purchaseTransaction);
        when(purchaseTransactionMapper.toDto(purchaseTransaction)).thenReturn(
            StorePurchaseResponse.builder().transactionId("tx-123").build()
        );
        when(purchaseTransactionRepository.save(any())).thenReturn(purchaseTransaction);

        StorePurchaseResponse response = purchase.storePurchase(request);
        assertNotNull(response);
        assertEquals("tx-123", response.getTransactionId());
    }

    @Test
    void testStorePurchaseCurrencyNotFound() {
        StorePurchaseRequest request = StorePurchaseRequest.builder()
            .amount(new BigDecimal("100.00"))
            .description("Test purchase")
            .purchaseDate(LocalDateTime.now())
            .build();
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.empty());
        when(purchaseTransactionMapper.toDomain(request)).thenReturn(mock(net.mycompany.commerce.purchase.domain.model.PurchaseTransaction.class));
        assertThrows(DataBaseNotFoundException.class, () -> purchase.storePurchase(request));
    }
}