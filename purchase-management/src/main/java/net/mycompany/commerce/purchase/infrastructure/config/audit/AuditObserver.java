package net.mycompany.commerce.purchase.infrastructure.config.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransactionAudit;
import net.mycompany.commerce.purchase.infrastructure.repository.PurchaseTransactionAuditRepository;

import java.time.LocalDateTime;

@Component
public class AuditObserver implements TransactionObserver {
    private final PurchaseTransactionAuditRepository auditRepository;

    public AuditObserver(PurchaseTransactionAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onPurchaseTransactionChanged(PurchaseTransaction transaction, AuditOperation operation) {
        PurchaseTransactionAudit audit = new PurchaseTransactionAudit(
            transaction,
            operation.name(),
            "SystemUser", //should be replaced with actual user info
            LocalDateTime.now()
        );
        auditRepository.save(audit);
    }
}
