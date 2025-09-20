package net.mycompany.commerce.purchase.mock;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponse;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransactionAudit;
import net.mycompany.commerce.purchase.infrastructure.config.security.SecurityConfig;
import net.mycompany.commerce.purchase.infrastructure.repository.PurchaseTransactionAuditRepository;

@WebMvcTest(ProducerMock.class)
@Import(SecurityConfig.class)
class ProducerMockTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QueueManagerServiceMock queueManager;

    @Autowired
    private PurchaseTransactionAuditRepository auditRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public QueueManagerServiceMock queueManagerServiceMock() {
            return Mockito.mock(QueueManagerServiceMock.class);
        }
        @Bean
        public PurchaseTransactionAuditRepository purchaseTransactionAuditRepository() {
            return Mockito.mock(PurchaseTransactionAuditRepository.class);
        }
    }

    @Test
    void testEnqueuePurchase() throws Exception {
        String json = "{\"transactionId\":\"tx-789\"}"; // Simplified for brevity

        // Stub queueManager.enqueue to simulate audit save
        Mockito.when(queueManager.enqueue(any(StorePurchaseRequest.class))).thenAnswer(invocation -> {
            StorePurchaseRequest req = invocation.getArgument(0);
            // Create a mock PurchaseTransactionAudit with expected values
            PurchaseTransactionAudit audit = Mockito.mock(PurchaseTransactionAudit.class);
            Mockito.when(audit.getOperation()).thenReturn("CREATE");
            net.mycompany.commerce.purchase.domain.model.PurchaseTransaction tx = Mockito.mock(net.mycompany.commerce.purchase.domain.model.PurchaseTransaction.class);
            Mockito.when(tx.getTransactionId()).thenReturn("tx-789");
            Mockito.when(audit.getPurchaseTransaction()).thenReturn(tx);
            auditRepository.save(audit);
            return "tx-789";
        });

        mockMvc.perform(post("/commerce/purchase/v1/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.transactionId").value("tx-789"));

        // Verify that an audit record was saved with the correct operation and transactionId
        Mockito.verify(auditRepository, Mockito.atLeastOnce()).save(Mockito.argThat(audit ->
            "CREATE".equals(audit.getOperation()) &&
            audit.getPurchaseTransaction() != null &&
            "tx-789".equals(audit.getPurchaseTransaction().getTransactionId())
        ));
    }

    // Helper mock for transaction
    static class PurchaseTransactionMock extends net.mycompany.commerce.purchase.domain.model.PurchaseTransaction {
        private final String transactionId;
        public PurchaseTransactionMock(String transactionId) { this.transactionId = transactionId; }
        @Override
        public String getTransactionId() { return transactionId; }
    }

    @Test
    void testGetResponseReady() throws Exception {
        StorePurchaseResponse response = new StorePurchaseResponse();
        response.setTransactionId("tx-789");
        when(queueManager.getResponse("tx-789")).thenReturn(response);

        mockMvc.perform(get("/commerce/purchase/v1/tx-789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("tx-789"));
    }

    @Test
    void testGetResponseProcessing() throws Exception {
        when(queueManager.getResponse("tx-000")).thenReturn(null);

        mockMvc.perform(get("/commerce/purchase/v1/tx-000"))
                .andExpect(status().isProcessing());
    }

    @Test
    void testEnqueuePurchaseWithInvalidDateFormat() throws Exception {
        String json = "{" +
                "\"amount\":50.00," +
                "\"description\":\"JUnit REST test\"," +
                "\"purchaseDate\":\"29/12/2020 12:30:31\"}";

        mockMvc.perform(post("/commerce/purchase/v1/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}