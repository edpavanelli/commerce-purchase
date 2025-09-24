package net.mycompany.commerce.purchasemgmt.domain.port;

import org.junit.jupiter.api.Test;

import net.mycompany.commerce.purchasemgmt.domain.port.TransactionIdGeneratorPort;

import static org.junit.jupiter.api.Assertions.*;

class TransactionIdGeneratorPortTest {
    @Test
    void testMockTransactionIdGeneratorPort() {
        TransactionIdGeneratorPort generator = () -> "tx-123";
        String id = generator.nextId();
        assertEquals("tx-123", id);
    }
}
