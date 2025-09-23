package net.mycompany.commerce.purchase.store.domain;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;
import net.mycompany.commerce.purchase.application.store.mapper.PurchaseTransactionMapper;
import net.mycompany.commerce.purchase.application.store.publisher.PurchasePublisher;
import net.mycompany.commerce.purchase.application.store.service.StorePurchaseService;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.domain.port.TransactionIdGeneratorPort;
import net.mycompany.commerce.purchase.domain.valueobject.TransactionId;
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
import org.mockito.ArgumentCaptor;

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
    private PurchasePublisher purchasePublisher;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        purchaseTransactionRepository = mock(PurchaseTransactionRepository.class);
        purchaseTransactionSubject = mock(PurchaseTransactionSubject.class);
        transactionObserver = mock(TransactionObserver.class);
        transactionIdGenerator = mock(TransactionIdGeneratorPort.class); // mock first
        purchaseTransactionMapper = new PurchaseTransactionMapper(transactionIdGenerator); // then use in mapper
        purchasePublisher = mock(PurchasePublisher.class);
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
            purchasePublisher,
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
        // Use the correct TransactionId type
        when(transactionIdGenerator.nextId()).thenReturn("tx-123");
        // Use the real PurchaseTransaction object created by the mapper
        PurchaseTransaction realPurchaseTransaction = purchaseTransactionMapper.toDomain(request);
        when(purchaseTransactionRepository.save(any())).thenReturn(realPurchaseTransaction);

        // Simulate storing purchase
        purchase.storePurchase(request);

        // Capture the response sent to publishResponse
        ArgumentCaptor<StorePurchaseResponseDto> responseCaptor = ArgumentCaptor.forClass(StorePurchaseResponseDto.class);
        verify(purchasePublisher).publishResponse(responseCaptor.capture());
        StorePurchaseResponseDto response = responseCaptor.getValue();
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
        // Use the real mapper, do not mock toDomain
        assertThrows(DataBaseNotFoundException.class, () -> purchase.storePurchase(request));
    }
}