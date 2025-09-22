package net.mycompany.commerce.purchase.application.exchange.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateRequestDto {

	@NotBlank(message = "Transaction ID cannot be blank")
	private String transactionId;
	
	@NotBlank(message = "Country's name cannot be blank")
	private String countryName;
	
}
