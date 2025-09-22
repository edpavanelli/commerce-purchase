package net.mycompany.commerce.purchase.application.exchange.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mycompany.commerce.common.dto.CurrencyDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateResponseDto {
	private String transactionId;
	private String description;
	private CurrencyDto purchaseCurrency;
	private BigDecimal purchaseAmount;
	private LocalDate transactionDate;
	private CurrencyDto targetCurrency;
	private BigDecimal targetAmount;
	private BigDecimal exchangeRate;
}