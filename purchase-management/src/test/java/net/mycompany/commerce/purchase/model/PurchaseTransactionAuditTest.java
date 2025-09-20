package net.mycompany.commerce.purchase.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PurchaseTransactionAuditTest {
    @Test
    void testEntityConstructionAndGettersSetters() {
        PurchaseTransaction transaction = new PurchaseTransaction();
        String operation = "CREATE";
        String changedBy = "TestUser";
        LocalDateTime changedDate = LocalDateTime.now();

        PurchaseTransactionAudit audit = new PurchaseTransactionAudit(transaction, operation, changedBy, changedDate);
        audit.setId(1L);
        assertEquals(1L, audit.getId());
        assertEquals(transaction, audit.getPurchaseTransaction());
        assertEquals(operation, audit.getOperation());
        assertEquals(changedBy, audit.getChangedBy());
        assertEquals(changedDate, audit.getChangedDate());
    }

    @Test
    void testDefaultValues() {
        PurchaseTransactionAudit audit = new PurchaseTransactionAudit();
        assertEquals("SystemUser", audit.getChangedBy());
        assertNotNull(audit.getChangedDate());
    }
}
