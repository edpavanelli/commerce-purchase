package net.mycompany.commerce.purchase.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import net.mycompany.commerce.purchase.domain.valueobject.TransactionId;

@Entity
@Table(name = "purchase_transaction_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "PurchaseTransaction", description = "Represents a purchase transaction in the system.")
public class PurchaseTransaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the purchase transaction.", example = "1001")
    private Long id;

    @Embedded
    @NotNull
    @Schema(description = "Transaction ID value object.")
    private TransactionId transactionId;

    @Column(precision = 15, scale = 2, nullable = false)
    @Digits(integer = 13, fraction = 2)
    @Positive(message = "Amount must be positive")
    @NotNull
    @Schema(description = "Amount of the purchase.", example = "250.00")
    private BigDecimal amount;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", nullable = false)
    @NotNull
    @Schema(description = "Currency used for the purchase.")
    private Currency currency;

    @Column(nullable = false)
    @NotNull
    @Schema(description = "Date of the purchase.", example = "2025-09-22")
    private LocalDate purchaseDate;

    @Column(length = 80)
    @Size(max = 80)
    @Schema(description = "Description of the purchase.", example = "Smartphone purchase")
    private String description;
}