package net.mycompany.commerce.purchase.store.dto;

import java.io.Serializable;
import java.util.UUID;

public class StorePurchaseResponse implements Serializable {
    private String transactionId;

    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
