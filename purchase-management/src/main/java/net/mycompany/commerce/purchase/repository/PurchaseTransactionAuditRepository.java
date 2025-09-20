package net.mycompany.commerce.purchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mycompany.commerce.purchase.model.PurchaseTransactionAudit;

@Repository
public interface PurchaseTransactionAuditRepository extends JpaRepository<PurchaseTransactionAudit, Long> {
}