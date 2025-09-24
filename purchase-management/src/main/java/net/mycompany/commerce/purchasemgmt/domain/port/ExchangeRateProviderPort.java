package net.mycompany.commerce.purchasemgmt.domain.port;


import java.util.List;

import net.mycompany.commerce.purchasemgmt.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import reactor.core.publisher.Mono;

public interface ExchangeRateProviderPort {
	
	public Mono<List<ExchangeRate>> getTreasuryExchangeRateFromRestClient(TreasuryExchangeRateFilterDto treasuryExchangeRateFilter);
	
}
