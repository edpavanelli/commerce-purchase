package net.mycompany.commerce.purchase.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchase_transaction_tb")
public class PurchaseTransaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    @NotNull
    private UUID transactionId;

    @Column(precision = 15, scale = 2, nullable = false)
    @Digits(integer = 13, fraction = 2)
    @NotNull
    private BigDecimal amount;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", nullable = false)
    @NotNull
    private Currency currency;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime purchaseDate;

    @Column(length = 80)
    @Size(max = 80)
    private String description;

    // No-argument constructor
    public PurchaseTransaction() {
        this.transactionId = UUID.randomUUID();
    }

    // All-argument constructor
    public PurchaseTransaction(Long id, BigDecimal amount, Currency currency, LocalDateTime purchaseDate, String description) {
        this.id = id;
        this.transactionId = UUID.randomUUID();
        this.amount = amount;
        this.currency = currency;
        this.purchaseDate = purchaseDate;
        this.description = description;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getTransactionId() { return this.transactionId;}
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}