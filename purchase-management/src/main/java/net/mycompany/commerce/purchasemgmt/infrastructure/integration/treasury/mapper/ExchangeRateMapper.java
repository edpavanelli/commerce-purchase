package net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchasemgmt.domain.model.Currency;
import net.mycompany.commerce.purchasemgmt.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.common.TreasuryApiConstants;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.ExchangeRateDto;

@Component
public class ExchangeRateMapper {

	
	public ExchangeRate toDomain(ExchangeRateDto dto) {
        return new ExchangeRate(
                Currency.builder()
					.code("")
					.name(dto.getCurrency())
					.country(dto.getCountry())
					.build(),
                new BigDecimal(dto.getExchangeRateAmount()), 
                LocalDate.parse(dto.getEffectiveDate(), TreasuryApiConstants.TREASURY_DATE_FORMATTER)
        );
    }

    public List<ExchangeRate> toDomainList(List<ExchangeRateDto> dtos) {
        return dtos.stream().map(this::toDomain).toList();
    }
}
