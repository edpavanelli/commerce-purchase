package net.mycompany.commerce.common.mapper;

import org.springframework.stereotype.Component;

import net.mycompany.commerce.common.dto.CurrencyDto;
import net.mycompany.commerce.purchase.domain.model.Currency;



@Component
public class CurrencyMapper {

	public Currency toDomain(CurrencyDto dto) {
    	return Currency.builder()
				.code(dto.getCode())
				.name(dto.getName())
				.country(dto.getCountry())
				.build();
    }

    public CurrencyDto toDto(Currency currency) {
       		return CurrencyDto.builder()
				.code(currency.getCode())
				.name(currency.getName())
				.country(currency.getCountry())
				.build();
    }
}
