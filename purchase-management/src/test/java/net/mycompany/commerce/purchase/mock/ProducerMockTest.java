package net.mycompany.commerce.purchase.mock;

import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import net.mycompany.commerce.purchase.security.SecurityConfig;

@WebMvcTest(ProducerMock.class)
@Import(SecurityConfig.class)
class ProducerMockTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QueueManagerServiceMock queueManager;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public QueueManagerServiceMock queueManagerServiceMock() {
            return Mockito.mock(QueueManagerServiceMock.class);
        }
    }

    @Test
    void testEnqueuePurchase() throws Exception {
        StorePurchaseRequest request = new StorePurchaseRequest();
        request.setAmount(new BigDecimal("50.00"));
        request.setDescription("JUnit REST test");
        request.setPurchaseDate(LocalDateTime.now());
        when(queueManager.enqueue(any(StorePurchaseRequest.class))).thenReturn("tx-789");

        String json = "{" +
                "\"amount\":50.00," +
                "\"description\":\"JUnit REST test\"," +
                "\"purchaseDate\":\"2025-09-19T12:00:00\"}";

        mockMvc.perform(post("/commerce/purchase/v1/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.transactionId").value("tx-789"));
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