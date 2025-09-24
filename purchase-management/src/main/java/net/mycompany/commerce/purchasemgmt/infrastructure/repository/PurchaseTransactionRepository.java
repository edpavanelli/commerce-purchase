package net.mycompany.commerce.purchasemgmt.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mycompany.commerce.purchasemgmt.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchasemgmt.domain.valueobject.TransactionId;

import java.util.Optional;

@Repository
public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, Long> {
    Optional<PurchaseTransaction> findByTransactionId(TransactionId transactionId);
}