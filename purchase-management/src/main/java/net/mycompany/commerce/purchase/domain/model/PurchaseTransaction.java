package net.mycompany.commerce.purchase.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

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
@NoArgsConstructor
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
    
    public PurchaseTransaction(@NotNull TransactionId transactionId,
			@Digits(integer = 13, fraction = 2) @Positive(message = "Amount must be positive") @NotNull BigDecimal amount,
			@NotNull Currency currency, @NotNull LocalDate purchaseDate, @Size(max = 80) String description) {
		super();
		this.transactionId = transactionId;
		this.setAmount(amount);
		this.currency = currency;
		this.purchaseDate = purchaseDate;
		this.description = description;
	}

    

    public void setAmount(BigDecimal amount) {
        if (amount != null) {
            this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        } else {
            this.amount = BigDecimal.ZERO;
        }
    }



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public TransactionId getTransactionId() {
		return transactionId;
	}



	public void setTransactionId(TransactionId transactionId) {
		this.transactionId = transactionId;
	}



	public Currency getCurrency() {
		return currency;
	}



	public void setCurrency(Currency currency) {
		this.currency = currency;
	}



	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}



	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public BigDecimal getAmount() {
		return amount;
	}



	@Override
	public String toString() {
		return "PurchaseTransaction [id=" + id + ", transactionId=" + transactionId + ", amount=" + amount
				+ ", currency=" + currency + ", purchaseDate=" + purchaseDate + ", description=" + description + "]";
	}



	@Override
	public int hashCode() {
		return Objects.hash(amount, currency, description, id, purchaseDate, transactionId);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PurchaseTransaction other = (PurchaseTransaction) obj;
		return Objects.equals(amount, other.amount) && Objects.equals(currency, other.currency)
				&& Objects.equals(description, other.description) && Objects.equals(id, other.id)
				&& Objects.equals(purchaseDate, other.purchaseDate)
				&& Objects.equals(transactionId, other.transactionId);
	}
    
    
    


}