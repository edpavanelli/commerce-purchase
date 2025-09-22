package net.mycompany.commerce.purchase.domain.port;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionIdGeneratorPortTest {
    @Test
    void testMockTransactionIdGeneratorPort() {
        TransactionIdGeneratorPort generator = () -> "tx-123";
        String id = generator.nextId();
        assertEquals("tx-123", id);
    }
}
