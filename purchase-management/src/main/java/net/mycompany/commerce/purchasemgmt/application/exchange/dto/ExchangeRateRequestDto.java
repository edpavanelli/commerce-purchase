package net.mycompany.commerce.purchasemgmt.application.exchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ExchangeRateRequestDto", description = "Request data for currency exchange conversion.")
public class ExchangeRateRequestDto {

	@Schema(description = "Unique identifier for the purchase transaction.", example = "TX123456789")
	@NotBlank(message = "Transaction ID cannot be blank")
	private String transactionId;

	
	@Schema(description = "Target country for currency conversion.", example = "Brazil")
	@NotBlank(message = "Country name cannot be blank")
	private String countryName;
}