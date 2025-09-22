package net.mycompany.commerce.purchase.store.consumer;

import net.mycompany.commerce.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchase.application.store.consumer.PurchaseConsumer;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;
import net.mycompany.commerce.purchase.application.store.service.StorePurchaseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.*;

class PurchaseConsumerTest {
    private QueueManagerServiceMock queueManager;
    private StorePurchaseService purchaseService;
    private PurchaseConsumer consumer;
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        queueManager = mock(QueueManagerServiceMock.class);
        purchaseService = mock(StorePurchaseService.class);
        applicationContext = mock(ApplicationContext.class);
        consumer = new PurchaseConsumer(queueManager, purchaseService, applicationContext);
    }

    @Test
    void testStorePurchaseCallsDependencies() {
        StorePurchaseRequestDto request = StorePurchaseRequestDto.builder()
            .amount(new java.math.BigDecimal("100.00"))
            .description("desc")
            .purchaseDate(java.time.LocalDate.now())
            .build();
        StorePurchaseResponseDto response = StorePurchaseResponseDto.builder()
            .transactionId("tx-456")
            .build();
        when(purchaseService.storePurchase(request)).thenReturn(response);

        consumer.storePurchase(request);

        verify(purchaseService, times(1)).storePurchase(request);
        verify(queueManager, times(1)).putResponse(response);
    }
}