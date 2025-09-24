package net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryExchangeRateResponseDto {

	
	private List<ExchangeRateDto> data;
	
}
