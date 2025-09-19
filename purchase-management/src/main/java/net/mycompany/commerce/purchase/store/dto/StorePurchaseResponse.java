package net.mycompany.commerce.purchase.store.dto;

import java.io.Serializable;
import java.util.UUID;

public class StorePurchaseResponse implements Serializable {
    private UUID transactionId;

    // Getters and setters
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
}
