package net.mycompany.commerce.purchase.store.dto;

import jakarta.validation.constraints.*;
import net.mycompany.commerce.purchase.validators.USDateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StorePurchaseRequest implements Serializable {
    @NotNull
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amount;

    @Size(max = 50, message = "description size exceeded")
    private String description;

    @NotNull(message = "invalid date pattern")
    @USDateTimeFormat
    private LocalDateTime purchaseDate;

    // Getters and setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
}