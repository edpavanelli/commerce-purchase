package net.mycompany.commerce.purchase.infrastructure.config.audit;

import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;

public interface TransactionObserver {
    void onPurchaseTransactionChanged(PurchaseTransaction transaction, AuditOperation operation);
}
