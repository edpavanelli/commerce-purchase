package net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {
	
    private String country;
    private String currency;
    private String exchangeRate;
    private String effectiveDate;

}
