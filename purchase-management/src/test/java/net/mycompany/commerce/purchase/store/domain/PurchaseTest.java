package net.mycompany.commerce.purchase.store.domain;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;
import net.mycompany.commerce.purchase.application.store.mapper.PurchaseTransactionMapper;
import net.mycompany.commerce.purchase.application.store.service.StorePurchaseService;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.port.TransactionIdGeneratorPort;
import net.mycompany.commerce.purchase.infrastructure.config.audit.PurchaseTransactionSubject;
import net.mycompany.commerce.purchase.infrastructure.config.audit.TransactionObserver;
import net.mycompany.commerce.purchase.infrastructure.config.exception.DataBaseNotFoundException;
import net.mycompany.commerce.purchase.infrastructure.repository.CurrencyRepository;
import net.mycompany.commerce.purchase.infrastructure.repository.PurchaseTransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class PurchaseTest {
    private CurrencyRepository currencyRepository;
    private PurchaseTransactionRepository purchaseTransactionRepository;
    private PurchaseTransactionSubject purchaseTransactionSubject;
    private TransactionObserver transactionObserver;
    private StorePurchaseService purchase;
    private PurchaseTransactionMapper purchaseTransactionMapper;
    private TransactionIdGeneratorPort transactionIdGenerator;
    private Environment environment;
    private String dataBaseNotFoundMessage;
    private String defaultCurrencyCode;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        purchaseTransactionRepository = mock(PurchaseTransactionRepository.class);
        purchaseTransactionSubject = mock(PurchaseTransactionSubject.class);
        transactionObserver = mock(TransactionObserver.class);
        purchaseTransactionMapper = mock(PurchaseTransactionMapper.class);
        transactionIdGenerator = mock(TransactionIdGeneratorPort.class);
        environment = mock(Environment.class);
        dataBaseNotFoundMessage = "Not found";
        defaultCurrencyCode = "USD";
        purchase = new StorePurchaseService(
            purchaseTransactionRepository,
            currencyRepository,
            environment,
            purchaseTransactionSubject,
            transactionObserver,
            purchaseTransactionMapper,
            dataBaseNotFoundMessage,
            defaultCurrencyCode
        );
    }

    @Test
    void testStorePurchaseSuccess() {
        StorePurchaseRequestDto request = StorePurchaseRequestDto.builder()
            .amount(new BigDecimal("100.00"))
            .description("Test purchase")
            .purchaseDate(LocalDate.now())
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
            StorePurchaseResponseDto.builder().transactionId("tx-123").build()
        );
        when(purchaseTransactionRepository.save(any())).thenReturn(purchaseTransaction);

        StorePurchaseResponseDto response = purchase.storePurchase(request);
        assertNotNull(response);
        assertEquals("tx-123", response.getTransactionId());
    }

    @Test
    void testStorePurchaseCurrencyNotFound() {
        StorePurchaseRequestDto request = StorePurchaseRequestDto.builder()
            .amount(new BigDecimal("100.00"))
            .description("Test purchase")
            .purchaseDate(LocalDate.now())
            .build();
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.empty());
        when(purchaseTransactionMapper.toDomain(request)).thenReturn(mock(net.mycompany.commerce.purchase.domain.model.PurchaseTransaction.class));
        assertThrows(DataBaseNotFoundException.class, () -> purchase.storePurchase(request));
    }
}