package net.mycompany.commerce.purchase.infrastructure.config.audit;

import net.mycompany.commerce.purchase.application.port.out.AuditEvent;

public interface TransactionObserver {
    void onPurchaseTransactionChanged(AuditEvent event);
}
