package net.mycompany.commerce.purchase.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchase_transaction_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseTransaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    @NotNull
    private String transactionId;

    @Column(precision = 15, scale = 2, nullable = false)
    @Digits(integer = 13, fraction = 2)
    @Positive(message = "Amount must be positive")
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

   
}