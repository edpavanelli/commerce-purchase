package net.mycompany.commerce.purchase.domain.port;


import java.util.List;

import net.mycompany.commerce.purchase.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import reactor.core.publisher.Mono;

public interface ExchangeRateProviderPort {
	
	public Mono<List<ExchangeRate>> getTreasuryExchangeRateFromRestClient(TreasuryExchangeRateFilterDto treasuryExchangeRateFilter);
	
}
