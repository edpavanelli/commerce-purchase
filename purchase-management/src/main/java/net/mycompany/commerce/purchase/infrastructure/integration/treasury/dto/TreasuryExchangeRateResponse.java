package net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryExchangeRateResponse {

	
	private List<ExchangeRate> exchangeRateList;
	
}
