package net.mycompany.commerce.purchase.model;

import org.junit.jupiter.api.Test;

import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;

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

class PurchaseTransactionAudit {
    private Long id;
    private PurchaseTransaction purchaseTransaction;
    private String operation;
    private String changedBy = "SystemUser";
    private java.time.LocalDateTime changedDate = java.time.LocalDateTime.now();
    public PurchaseTransactionAudit() {}
    public PurchaseTransactionAudit(PurchaseTransaction tx, String op, String changedBy, java.time.LocalDateTime changedDate) {
        this.purchaseTransaction = tx;
        this.operation = op;
        this.changedBy = changedBy;
        this.changedDate = changedDate;
    }
    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public PurchaseTransaction getPurchaseTransaction() { return purchaseTransaction; }
    public String getOperation() { return operation; }
    public String getChangedBy() { return changedBy; }
    public java.time.LocalDateTime getChangedDate() { return changedDate; }
}