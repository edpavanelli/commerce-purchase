package net.mycompany.commerce.purchase.audit;

import net.mycompany.commerce.purchase.model.PurchaseTransaction;

public interface TransactionObserver {
    void onPurchaseTransactionChanged(PurchaseTransaction transaction, AuditOperation operation);
}
