package net.mycompany.commerce.purchase.infrastructure.config.audit;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import net.mycompany.commerce.purchase.application.port.out.AuditEvent;
import net.mycompany.commerce.purchase.application.port.out.AuditEventPublisher;


@Component
public class AuditObserver implements TransactionObserver {
	private final AuditEventPublisher eventPublisher;

    public AuditObserver(AuditEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    @TransactionalEventListener
    public void onPurchaseTransactionChanged(AuditEvent event) {
        
        eventPublisher.publishAuditEvent(event);
    }
}
