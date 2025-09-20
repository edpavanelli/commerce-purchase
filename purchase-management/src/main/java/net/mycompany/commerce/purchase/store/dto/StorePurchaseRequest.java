package net.mycompany.commerce.purchase.store.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import net.mycompany.commerce.purchase.validators.USDateTimeFormat;

public class StorePurchaseRequest implements Serializable {
    @NotNull
    @Digits(integer = 13, fraction = 2, message = "Wrong amount format")
    @Positive(message = "Amount must be positive")
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