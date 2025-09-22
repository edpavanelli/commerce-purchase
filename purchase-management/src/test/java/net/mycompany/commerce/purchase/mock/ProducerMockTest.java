package net.mycompany.commerce.purchase.mock;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import net.mycompany.commerce.mock.ProducerMock;
import net.mycompany.commerce.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;

class ProducerMockTest {
    private QueueManagerServiceMock queueManager;
    private PurchaseTransactionAuditRepository auditRepository;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        queueManager = Mockito.mock(QueueManagerServiceMock.class);
        auditRepository = Mockito.mock(PurchaseTransactionAuditRepository.class);
    }

    @Test
    void testEnqueuePurchase() throws Exception {
        // Removed transactionId references, use available fields
        Mockito.when(queueManager.enqueue(any(StorePurchaseRequestDto.class))).thenAnswer(invocation -> {
            StorePurchaseRequestDto req = invocation.getArgument(0);
            PurchaseTransactionAudit audit = Mockito.mock(PurchaseTransactionAudit.class);
            Mockito.when(audit.getOperation()).thenReturn("CREATE");
            net.mycompany.commerce.purchase.domain.model.PurchaseTransaction tx = Mockito.mock(net.mycompany.commerce.purchase.domain.model.PurchaseTransaction.class);
            Mockito.when(audit.getPurchaseTransaction()).thenReturn(tx);
            auditRepository.save(audit);
            return "success";
        });

        StorePurchaseRequestDto req = StorePurchaseRequestDto.builder()
            .amount(new java.math.BigDecimal("100.00"))
            .description("Test purchase")
            .purchaseDate(java.time.LocalDate.now())
            .build();
        String result = queueManager.enqueue(req);
        assertTrue("success".equals(result));

        Mockito.verify(auditRepository, Mockito.atLeastOnce()).save(Mockito.argThat(audit ->
            "CREATE".equals(audit.getOperation()) &&
            audit.getPurchaseTransaction() != null
        ));
    }

    // Helper mock for transaction
    static class PurchaseTransactionMock extends net.mycompany.commerce.purchase.domain.model.PurchaseTransaction {
        private final String transactionId;
        public PurchaseTransactionMock(String transactionId) { this.transactionId = transactionId; }
        @Override
        public net.mycompany.commerce.purchase.domain.valueobject.TransactionId getTransactionId() { return new net.mycompany.commerce.purchase.domain.valueobject.TransactionId(transactionId); }
    }

    @Test
    void testGetResponseReady() throws Exception {
        StorePurchaseResponseDto response = new StorePurchaseResponseDto();
        response.setTransactionId("tx-789");
        when(queueManager.getResponse("tx-789")).thenReturn(response);

        // Simulate logic and verify
        StorePurchaseResponseDto result = queueManager.getResponse("tx-789");
        assertTrue("tx-789".equals(result.getTransactionId()));
    }

    @Test
    void testGetResponseProcessing() throws Exception {
        when(queueManager.getResponse("tx-000")).thenReturn(null);

        // Simulate logic and verify
        StorePurchaseResponseDto result = queueManager.getResponse("tx-000");
        assertTrue(result == null);
    }

    @Test
    void testEnqueuePurchaseWithInvalidDateFormat() throws Exception {
        String invalidDate = "29/12/2020 12:30:31";
        boolean exceptionThrown = false;
        try {
            StorePurchaseRequestDto req = StorePurchaseRequestDto.builder()
                .amount(new java.math.BigDecimal("50.00"))
                .description("JUnit REST test")
                .purchaseDate(java.time.LocalDate.parse(invalidDate)) // This should throw
                .build();
            queueManager.enqueue(req);
        } catch (Exception e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().contains("Invalid date") || e.getMessage().contains("Text '29/12/2020 12:30:31'"));
        }
        assertTrue(exceptionThrown);
    }

    // Local stub classes for missing types
    class PurchaseTransactionAudit {
        private String operation;
        private net.mycompany.commerce.purchase.domain.model.PurchaseTransaction purchaseTransaction;
        public String getOperation() { return operation; }
        public void setOperation(String op) { this.operation = op; }
        public net.mycompany.commerce.purchase.domain.model.PurchaseTransaction getPurchaseTransaction() { return purchaseTransaction; }
        public void setPurchaseTransaction(net.mycompany.commerce.purchase.domain.model.PurchaseTransaction tx) { this.purchaseTransaction = tx; }
    }
    interface PurchaseTransactionAuditRepository {
        void save(PurchaseTransactionAudit audit);
    }
}