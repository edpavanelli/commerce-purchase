package net.mycompany.commerce.purchase.infrastructure.config.audit;

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
        
    	PurchaseTransactionAudit audit = PurchaseTransactionAudit.builder()
				.purchaseTransaction(transaction)
				.operation(operation.name())
				.changedBy("SystemUser") //should be replaced with actual user info
				.changedDate(LocalDateTime.now())
				.build();
    	
        auditRepository.save(audit);
    }
}
