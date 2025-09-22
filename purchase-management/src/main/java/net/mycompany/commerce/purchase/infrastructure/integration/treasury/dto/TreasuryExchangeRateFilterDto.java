package net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryExchangeRateFilterDto {

	private String country;
	private LocalDate requestDateFrom;
	private LocalDate requestDateTo;
	private TreasuryExchangeRateSortDto sortBy;
}
