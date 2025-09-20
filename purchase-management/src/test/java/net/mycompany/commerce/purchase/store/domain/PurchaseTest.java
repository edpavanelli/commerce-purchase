package net.mycompany.commerce.purchase.store.domain;

import net.mycompany.commerce.purchase.exception.DataBaseNotFoundException;
import net.mycompany.commerce.purchase.model.Currency;
import net.mycompany.commerce.purchase.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.repository.CurrencyRepository;
import net.mycompany.commerce.purchase.repository.PurchaseTransactionRepository;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurchaseTest {
    private CurrencyRepository currencyRepository;
    private PurchaseTransactionRepository purchaseTransactionRepository;
    private Purchase purchase;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        purchaseTransactionRepository = mock(PurchaseTransactionRepository.class);
        purchase = new Purchase(purchaseTransactionRepository, currencyRepository, "USD");
    }

    @Test
    void testNewPurchaseSuccess() {
        StorePurchaseRequest request = new StorePurchaseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Test purchase");
        request.setPurchaseDate(LocalDateTime.now());
        Currency currency = new Currency("USD", "US Dollar", "United States");
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currency));
        when(purchaseTransactionRepository.save(any(PurchaseTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StorePurchaseResponse response = purchase.newPurchase(request, "tx-123");
        assertNotNull(response);
        assertEquals("tx-123", response.getTransactionId());
    }

    @Test
    void testNewPurchaseCurrencyNotFound() {
        StorePurchaseRequest request = new StorePurchaseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Test purchase");
        request.setPurchaseDate(LocalDateTime.now());
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.empty());

        assertThrows(DataBaseNotFoundException.class, () -> purchase.newPurchase(request, "tx-123"));
    }
}
