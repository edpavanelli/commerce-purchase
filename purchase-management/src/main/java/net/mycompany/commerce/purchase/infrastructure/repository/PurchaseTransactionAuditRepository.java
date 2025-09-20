package net.mycompany.commerce.purchase.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mycompany.commerce.purchase.domain.model.PurchaseTransactionAudit;

@Repository
public interface PurchaseTransactionAuditRepository extends JpaRepository<PurchaseTransactionAudit, Long> {
}