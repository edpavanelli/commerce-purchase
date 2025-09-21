package net.mycompany.commerce.purchase.domain.valueobject;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.mycompany.commerce.purchase.domain.model.Currency;

@AllArgsConstructor
@Builder
@Getter
public class ExchangeRate {

	private final Currency currency;
	private final BigDecimal exchangeRateAmount;
    private final LocalDate effectiveDate;
    
    
}
