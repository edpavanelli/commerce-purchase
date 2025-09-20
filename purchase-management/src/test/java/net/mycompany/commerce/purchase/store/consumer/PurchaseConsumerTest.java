package net.mycompany.commerce.purchase.store.consumer;

import net.mycompany.commerce.purchase.domain.Purchase;
import net.mycompany.commerce.purchase.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PurchaseConsumerTest {
    private QueueManagerServiceMock queueManager;
    private Purchase purchaseService;
    private PurchaseConsumer consumer;

    @BeforeEach
    void setUp() {
        queueManager = mock(QueueManagerServiceMock.class);
        purchaseService = mock(Purchase.class);
        consumer = new PurchaseConsumer(queueManager, purchaseService, null);
    }

    @Test
    void testStorePurchaseCallsDependencies() {
        StorePurchaseRequest request = new StorePurchaseRequest();
        String transactionId = "tx-456";
        StorePurchaseResponse response = new StorePurchaseResponse();
        response.setTransactionId(transactionId);
        when(purchaseService.newPurchase(request, transactionId)).thenReturn(response);

        consumer.storePurchase(transactionId, request);

        verify(purchaseService, times(1)).newPurchase(request, transactionId);
        verify(queueManager, times(1)).putResponse(eq(transactionId), any(StorePurchaseResponse.class));
    }
}
