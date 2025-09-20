package net.mycompany.commerce.purchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mycompany.commerce.purchase.model.PurchaseTransactionAudit;

public interface PurchaseTransactionAuditRepository extends JpaRepository<PurchaseTransactionAudit, Long> {
}
