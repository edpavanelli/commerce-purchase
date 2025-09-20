package net.mycompany.commerce.purchase.store.consumer;

import net.mycompany.commerce.purchase.domain.Purchase;
import net.mycompany.commerce.purchase.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.mapper.PurchaseTransactionMapper;
import net.mycompany.commerce.purchase.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PurchaseConsumerTest {
    private QueueManagerServiceMock queueManager;
    private Purchase purchaseService;
    private PurchaseConsumer consumer;
    private ApplicationContext applicationContext;
    private PurchaseTransactionMapper purchaseTransactionMapper;

    @BeforeEach
    void setUp() {
        queueManager = mock(QueueManagerServiceMock.class);
        purchaseService = mock(Purchase.class);
        applicationContext = mock(ApplicationContext.class);
        purchaseTransactionMapper = mock(PurchaseTransactionMapper.class);
        consumer = new PurchaseConsumer(queueManager, purchaseService, applicationContext, purchaseTransactionMapper);
    }

    @Test
    void testStorePurchaseCallsDependencies() {
        StorePurchaseRequest request = StorePurchaseRequest.builder()
            .amount(new java.math.BigDecimal("100.00"))
            .description("desc")
            .purchaseDate(java.time.LocalDateTime.now())
            .build();
        PurchaseTransaction purchaseTransaction = mock(PurchaseTransaction.class);
        StorePurchaseResponse response = StorePurchaseResponse.builder()
            .transactionId("tx-456")
            .build();
        when(purchaseTransactionMapper.toDomain(request)).thenReturn(purchaseTransaction);
        when(purchaseService.storePurchase(purchaseTransaction)).thenReturn(purchaseTransaction);
        when(purchaseTransactionMapper.toResponseDto(purchaseTransaction)).thenReturn(response);

        consumer.storePurchase(request);

        verify(purchaseService, times(1)).storePurchase(purchaseTransaction);
        verify(queueManager, times(1)).putResponse(response);
    }
}