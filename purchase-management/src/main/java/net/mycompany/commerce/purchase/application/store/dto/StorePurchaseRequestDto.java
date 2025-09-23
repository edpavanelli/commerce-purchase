package net.mycompany.commerce.purchase.application.store.dto;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mycompany.commerce.purchase.infrastructure.config.validator.USDateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "StorePurchaseRequestDto", description = "Request data for storing a purchase transaction.")
public class StorePurchaseRequestDto{
    @NotNull
    @Digits(integer = 13, fraction = 5, message = "Wrong amount format")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Amount of the purchase.", example = "150.00")
    private BigDecimal amount;

    @Size(max = 50, message = "Description size exceeded")
    @Schema(description = "Description of the purchase.", example = "Laptop purchase")
    private String description;

    @NotNull(message = "Invalid date pattern")
    @USDateTimeFormat
    @Schema(description = "Date of the purchase.", example = "2025-09-22")
    private LocalDate purchaseDate;
    
    
}