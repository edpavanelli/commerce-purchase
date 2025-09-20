package net.mycompany.commerce.purchase.store.domain;

import net.mycompany.commerce.purchase.audit.PurchaseTransactionSubject;
import net.mycompany.commerce.purchase.audit.TransactionObserver;
import net.mycompany.commerce.purchase.domain.Purchase;
import net.mycompany.commerce.purchase.exception.DataBaseNotFoundException;
import net.mycompany.commerce.purchase.mapper.PurchaseTransactionMapper;
import net.mycompany.commerce.purchase.model.Currency;
import net.mycompany.commerce.purchase.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.repository.CurrencyRepository;
import net.mycompany.commerce.purchase.repository.PurchaseTransactionRepository;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;

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
    private Purchase purchase;
    private PurchaseTransactionMapper purchaseTransactionMapper;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        purchaseTransactionRepository = mock(PurchaseTransactionRepository.class);
        purchaseTransactionSubject = mock(PurchaseTransactionSubject.class);
        transactionObserver = mock(TransactionObserver.class);
        purchaseTransactionMapper = new PurchaseTransactionMapper();
        purchase = new Purchase(
            purchaseTransactionRepository,
            currencyRepository,
            "USD",
            purchaseTransactionSubject,
            transactionObserver
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
        when(purchaseTransactionRepository.save(any(PurchaseTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseTransaction purchaseTransaction = purchaseTransactionMapper.toDomain(request);
        PurchaseTransaction result = purchase.storePurchase(purchaseTransaction);
        assertNotNull(result.getTransactionId());
        assertEquals(currency, result.getCurrency());
    }

    @Test
    void testStorePurchaseCurrencyNotFound() {
        StorePurchaseRequest request = StorePurchaseRequest.builder()
            .amount(new BigDecimal("100.00"))
            .description("Test purchase")
            .purchaseDate(LocalDateTime.now())
            .build();
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.empty());

        PurchaseTransaction purchaseTransaction = purchaseTransactionMapper.toDomain(request);
        assertThrows(DataBaseNotFoundException.class, () -> purchase.storePurchase(purchaseTransaction));
    }
}