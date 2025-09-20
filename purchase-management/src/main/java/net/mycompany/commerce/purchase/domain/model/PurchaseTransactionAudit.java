package net.mycompany.commerce.purchase.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_transaction_audit_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

}