package net.mycompany.commerce.purchase.infrastructure.config.audit;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import net.mycompany.commerce.purchase.application.port.out.AuditEvent;
import net.mycompany.commerce.purchase.application.port.out.AuditEventPublisher;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;


import java.time.LocalDateTime;

@Component
public class AuditObserver implements TransactionObserver {
	private final AuditEventPublisher eventPublisher;

    public AuditObserver(AuditEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    @TransactionalEventListener
    public void onPurchaseTransactionChanged(PurchaseTransaction transaction, AuditOperation operation) {

        AuditEvent event = new AuditEvent(
            transaction.getId().toString(),
            operation.name(),
            "SystemUser",  
            LocalDateTime.now()
        );

        
        eventPublisher.publishAuditEvent(event);
    }
}
