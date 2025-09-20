package net.mycompany.commerce.purchase.infrastructure.integration;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchase.domain.model.port.ExchangeRateProvider;
import net.mycompany.commerce.purchase.infrastructure.config.rest.TreasuryApiProperties;
import net.mycompany.commerce.purchase.infrastructure.config.rest.WebClientFactory;

import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TreasuryExchangeRateRestClient implements ExchangeRateProvider {

	private final WebClient webClient;
    private final TreasuryApiProperties props;

    public TreasuryExchangeRateRestClient(WebClientFactory webClientFactory, TreasuryApiProperties props) {
        this.props = props;
        this.webClient = webClientFactory.createJsonWebClient(props);
    }
	
	
	@Override
	public BigDecimal getRate(String fromCurrency, String toCurrency) {
		
		
		
	}

}
