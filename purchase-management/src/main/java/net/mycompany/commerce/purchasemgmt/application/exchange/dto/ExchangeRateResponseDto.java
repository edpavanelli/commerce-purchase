package net.mycompany.commerce.purchasemgmt.application.exchange.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mycompany.commerce.common.dto.CurrencyDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ExchangeRateResponseDto", description = "Response data for currency exchange conversion.")
public class ExchangeRateResponseDto {
    @Schema(description = "Unique identifier for the purchase transaction.", example = "TX123456789")
    private String transactionId;
    @Schema(description = "Description of the transaction.", example = "Purchase of electronics")
    private String description;
    @Schema(description = "Currency details for the purchase.")
    private CurrencyDto purchaseCurrency;
    @Schema(description = "Amount of the purchase in the original currency.", example = "100.00")
    private BigDecimal purchaseAmount;
    @Schema(description = "Date of the transaction.", example = "2025-09-22")
    private LocalDate transactionDate;
    @Schema(description = "Target country for currency conversion.", example = "Brazil")
    private String targetCountry;
    @Schema(description = "Amount in the target currency.", example = "500.00")
    private BigDecimal targetAmount;
    @Schema(description = "Exchange rate applied for conversion.", example = "5.00")
    private BigDecimal exchangeRate;
}