package net.mycompany.commerce.purchase.infrastructure.integration.treasury.rest;


import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import net.mycompany.commerce.common.dto.PaginationFiltersDto;
import net.mycompany.commerce.purchase.domain.port.ExchangeRateProviderPort;
import net.mycompany.commerce.purchase.domain.service.PurchaseDomainService;
import net.mycompany.commerce.purchase.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchase.infrastructure.config.rest.TreasuryApiProperties;
import net.mycompany.commerce.purchase.infrastructure.config.rest.WebClientFactory;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.common.TreasuryApiConstants;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.ExchangeRateDto;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateResponseDto;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateSortDto;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.mapper.ExchangeRateMapper;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TreasuryExchangeRateRestClient implements ExchangeRateProviderPort {

	private final WebClient webClient;
    private final TreasuryApiProperties properties;
    private final ExchangeRateMapper exchangeRateMapper;

    public TreasuryExchangeRateRestClient(WebClientFactory webClientFactory, 
    		TreasuryApiProperties properties,
    		ExchangeRateMapper exchangeRateMapper) {
        this.properties = properties;
        this.webClient = webClientFactory.createJsonWebClient(properties);
        this.exchangeRateMapper = exchangeRateMapper;
    }
	
	
	@Override
	public Mono<List<ExchangeRate>> getTreasuryExchangeRate(TreasuryExchangeRateFilterDto treasuryExchangeRateFilter, 
			TreasuryExchangeRateSortDto treasuryExchangeRateSort,
			PaginationFiltersDto paginationFilter) {
		
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
			            .path(properties.getPath())
			            // filtros
			            .queryParam("filter", String.format(
			                "effective_date:lte:%s,effective_date:gte:%s,country:eq:%s",
			                treasuryExchangeRateFilter.getRequestDateTo().format(TreasuryApiConstants.TREASURY_DATE_FORMATTER),
			                treasuryExchangeRateFilter.getRequestDateFrom().format(TreasuryApiConstants.TREASURY_DATE_FORMATTER),
			                treasuryExchangeRateFilter.getCountry()
			            ))
			            // ordenação
			            .queryParam("sort", "-" + treasuryExchangeRateSort.name().toLowerCase())
			            // paginação
			            .queryParam("page[number]", paginationFilter.getPageNumber())
			            .queryParam("page[size]", paginationFilter.getPageSize())
			            .build())
                .retrieve()
                .bodyToMono(TreasuryExchangeRateResponseDto.class)
                .map(resp -> exchangeRateMapper.toDomainList(resp.getData()));
                
		
	}

}
