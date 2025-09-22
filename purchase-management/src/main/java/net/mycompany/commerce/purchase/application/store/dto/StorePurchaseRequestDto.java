package net.mycompany.commerce.purchase.application.store.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.infrastructure.config.validator.USDateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorePurchaseRequestDto{
    @NotNull
    @Digits(integer = 13, fraction = 2, message = "Wrong amount format")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Size(max = 50, message = "description size exceeded")
    private String description;

    @NotNull(message = "invalid date pattern")
    @USDateTimeFormat
    private LocalDate purchaseDate;

   
}