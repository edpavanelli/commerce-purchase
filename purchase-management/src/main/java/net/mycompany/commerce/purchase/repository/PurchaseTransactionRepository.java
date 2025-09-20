package net.mycompany.commerce.purchase.repository;

import net.mycompany.commerce.purchase.model.PurchaseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, Long> {
    Optional<PurchaseTransaction> findByTransactionId(String transactionId);
}
