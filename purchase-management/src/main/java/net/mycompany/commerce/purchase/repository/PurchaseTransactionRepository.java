package net.mycompany.commerce.purchase.repository;

import net.mycompany.commerce.purchase.model.PurchaseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, Long> {
    Optional<PurchaseTransaction> findByTransactionId(String transactionId);
}
