package net.mycompany.commerce.purchase.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_transaction_audit_tb")
public class PurchaseTransactionAudit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_transaction_id", nullable = false)
    private PurchaseTransaction purchaseTransaction;

    @Column(nullable = false, length = 10)
    private String operation;

    @Column(nullable = false, length = 50)
    private String changedBy = "SystemUser";

    @Column(nullable = false)
    private LocalDateTime changedDate = LocalDateTime.now();

    public PurchaseTransactionAudit() {}

    public PurchaseTransactionAudit(PurchaseTransaction purchaseTransaction, String operation, String changedBy, LocalDateTime changedDate) {
        this.purchaseTransaction = purchaseTransaction;
        this.operation = operation;
        this.changedBy = changedBy;
        this.changedDate = changedDate;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PurchaseTransaction getPurchaseTransaction() { return purchaseTransaction; }
    public void setPurchaseTransaction(PurchaseTransaction purchaseTransaction) { this.purchaseTransaction = purchaseTransaction; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public LocalDateTime getChangedDate() { return changedDate; }
    public void setChangedDate(LocalDateTime changedDate) { this.changedDate = changedDate; }
}