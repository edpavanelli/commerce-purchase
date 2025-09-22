package net.mycompany.commerce.purchase.application.exchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ExchangeRateRequestDto", description = "Request data for currency exchange conversion.")
public class ExchangeRateRequestDto {

	@NotBlank(message = "Transaction ID cannot be blank")
	@Schema(description = "Unique identifier for the purchase transaction.", example = "TX123456789")
	private String transactionId;

	@NotBlank(message = "Country name cannot be blank")
	@Schema(description = "Target country for currency conversion.", example = "Brazil")
	private String countryName;
}