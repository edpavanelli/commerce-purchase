package net.mycompany.commerce.purchase.infrastructure.integration.treasury;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import net.mycompany.commerce.common.dto.PaginationFiltersDto;
import net.mycompany.commerce.common.util.DateUtils;
import net.mycompany.commerce.common.util.StringUtils;
import net.mycompany.commerce.purchase.domain.port.ExchangeRateProviderPort;
import net.mycompany.commerce.purchase.domain.service.PurchaseDomainService;
import net.mycompany.commerce.purchase.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchase.infrastructure.config.rest.TreasuryApiProperties;
import net.mycompany.commerce.purchase.infrastructure.config.rest.WebClientFactory;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.common.TreasuryApiConstants;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateResponseDto;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateSortDto;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.mapper.ExchangeRateMapper;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TreasuryExchangeRateProvider implements ExchangeRateProviderPort {
	
	private static final Logger log = LoggerFactory.getLogger(TreasuryExchangeRateProvider.class);

	private Environment env;
	private final WebClient webClient;
    private final TreasuryApiProperties properties;
    private final ExchangeRateMapper exchangeRateMapper;
    private final CacheManager cacheManager;
    

    public TreasuryExchangeRateProvider(Environment env,
    		WebClientFactory webClientFactory, 
    		TreasuryApiProperties properties,
    		ExchangeRateMapper exchangeRateMapper,
    		CacheManager cacheManager) {
    	this.env = env;
        this.properties = properties;
        this.webClient = webClientFactory.createJsonWebClient(properties);
        this.exchangeRateMapper = exchangeRateMapper;
        this.cacheManager = cacheManager;
    }
	
	
	@Override
	public Mono<List<ExchangeRate>> getTreasuryExchangeRateFromRestClient(TreasuryExchangeRateFilterDto treasuryExchangeRateFilter) {
		
		PaginationFiltersDto paginationFilter = PaginationFiltersDto.builder()
				.pageNumber(env.getProperty("api.service.treasury.pagination.number", Integer.class, 1))
				.pageSize(env.getProperty("api.service.treasury.pagination.size", Integer.class, 1))
				.build();
		
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
			            .path(properties.getPath())
			            // filtros
			            .queryParam("filter", String.format(
			                "effective_date:lte:%s,effective_date:gte:%s,country:eq:%s",
			                treasuryExchangeRateFilter.getRequestDateTo().format(TreasuryApiConstants.TREASURY_DATE_FORMATTER),
			                treasuryExchangeRateFilter.getRequestDateFrom().format(TreasuryApiConstants.TREASURY_DATE_FORMATTER),
			                StringUtils.capitalizeFirstLetter(treasuryExchangeRateFilter.getCountry())
			            ))
			            // ordenação
			            .queryParam("sort", "-" + treasuryExchangeRateFilter.getSortBy().name().toLowerCase())
			            // paginação
			            .queryParam("page[number]", paginationFilter.getPageNumber())
			            .queryParam("page[size]", paginationFilter.getPageSize())
			            .build())
                .retrieve()
                .bodyToMono(TreasuryExchangeRateResponseDto.class)
                .map(resp -> exchangeRateMapper.toDomainList(resp.getData()));
                
		
	}
	
	
	@EventListener(ApplicationReadyEvent.class)
    public void cacheExchangeRatesOnApplicationReady() {
		LocalDate now = LocalDate.now();
        List<String> countries = Arrays.asList(env.getProperty("environment.default.cuntries.exchange") .split(","));
        for (String country : countries) {
            try {
                TreasuryExchangeRateFilterDto filter = TreasuryExchangeRateFilterDto.builder()
                    .country(country)
                    .requestDateTo(now)
                    .requestDateFrom(DateUtils.getDateSixMonthsBack(now))
                    .sortBy(TreasuryExchangeRateSortDto.EFFECTIVE_DATE)
                    .build();
                List<ExchangeRate> exchangeRateList = this.getTreasuryExchangeRateFromRestClient(
                        filter).block();
                		
                if (exchangeRateList != null && !exchangeRateList.isEmpty()) {
                	cacheExchangeRate(StringUtils.capitalizeFirstLetter(country), exchangeRateList);
                } else {
                	cacheExchangeRate(StringUtils.capitalizeFirstLetter(country), new ArrayList<>());
                }	
                
            } catch (Exception e) {
                log.error("Exception fetching exchange rate for country {}: {}", country, e.getMessage());
            }
        }
    }

	@CachePut(value = "treasuryExchangeRateCache", key = "#country")
    public void cacheExchangeRate(String country, List<ExchangeRate> exchangeRateList) {
        // The cache will store a simple object or map with country, exchangeRate, effectiveDate
        log.info("Cached exchange rate for {}: ExchangeRate={}", country, exchangeRateList.toArray());
    }

	
	public List<ExchangeRate> getCachedExchangeRateList(String country) {
		Cache cache = cacheManager.getCache("treasuryExchangeRateCache");
		
        if (cache != null) {
            return cache.get(country, List.class);
        }
        
        return null;
        
    }
	

}
