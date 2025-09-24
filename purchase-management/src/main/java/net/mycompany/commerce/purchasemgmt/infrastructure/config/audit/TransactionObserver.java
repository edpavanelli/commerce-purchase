package net.mycompany.commerce.purchasemgmt.infrastructure.config.audit;

import net.mycompany.commerce.purchasemgmt.application.port.out.AuditEvent;

public interface TransactionObserver {
    void onPurchaseTransactionChanged(AuditEvent event);
}
