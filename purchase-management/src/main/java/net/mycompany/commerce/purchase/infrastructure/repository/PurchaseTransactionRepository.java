package net.mycompany.commerce.purchase.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, Long> {
    Optional<PurchaseTransaction> findByTransactionId(String transactionId);
}
