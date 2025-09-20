package net.mycompany.commerce.purchase.domain.model.port;


import java.util.List;

import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateResponse;
import reactor.core.publisher.Mono;

public interface ExchangeRateProvider {
	Mono<List<TreasuryExchangeRateResponse>> getTreasuryExchangeRate(String fromCurrency, String toCurrency);
}
