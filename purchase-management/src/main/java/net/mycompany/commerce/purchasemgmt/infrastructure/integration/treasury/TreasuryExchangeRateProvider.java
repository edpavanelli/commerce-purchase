package net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.mycompany.commerce.common.dto.PaginationFiltersDto;
import net.mycompany.commerce.common.util.DateUtils;
import net.mycompany.commerce.common.util.StringUtils;
import net.mycompany.commerce.purchasemgmt.domain.port.ExchangeRateProviderPort;
import net.mycompany.commerce.purchasemgmt.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.cache.CacheService;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.rest.TreasuryApiProperties;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.rest.WebClientFactory;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.common.TreasuryApiConstants;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.TreasuryExchangeRateResponseDto;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.TreasuryExchangeRateSortDto;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.mapper.ExchangeRateMapper;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TreasuryExchangeRateProvider implements ExchangeRateProviderPort {
	
	private static final Logger log = LoggerFactory.getLogger(TreasuryExchangeRateProvider.class);

	private Environment env;
	private final WebClient webClient;
    private final TreasuryApiProperties properties;
    private final ExchangeRateMapper exchangeRateMapper;
    private final CacheService cacheService;
    private final ReactiveCircuitBreaker treasuryBreaker;
    

    public TreasuryExchangeRateProvider(Environment env,
    		WebClientFactory webClientFactory, 
    		TreasuryApiProperties properties,
    		ExchangeRateMapper exchangeRateMapper,
    		CacheService cacheService,
    		ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory) {
    	this.env = env;
        this.properties = properties;
        this.webClient = webClientFactory.createJsonWebClient(properties);
        this.exchangeRateMapper = exchangeRateMapper;
        this.cacheService = cacheService;
        this.treasuryBreaker = circuitBreakerFactory.create("treasuryExchange");
    }
	
	
	@Override
	public Mono<List<ExchangeRate>> getTreasuryExchangeRateFromRestClient(TreasuryExchangeRateFilterDto treasuryExchangeRateFilter) {
		
		PaginationFiltersDto paginationFilter = PaginationFiltersDto.builder()
				.pageNumber(env.getProperty("api.service.treasury.pagination.number", Integer.class, 1))
				.pageSize(env.getProperty("api.service.treasury.pagination.size", Integer.class, 1))
				.build();
		
		
		
		
		/*return webClient.get()
				.uri(uriBuilder -> uriBuilder
			            .path(properties.getPath())
			            //filter
			            .queryParam("filter", String.format(
			                "effective_date:lte:%s,effective_date:gte:%s,country:eq:%s",
			                treasuryExchangeRateFilter.getRequestDateTo().format(TreasuryApiConstants.TREASURY_DATE_FORMATTER),
			                treasuryExchangeRateFilter.getRequestDateFrom().format(TreasuryApiConstants.TREASURY_DATE_FORMATTER),
			                StringUtils.capitalizeFirstLetter(treasuryExchangeRateFilter.getCountry())
			            ))
			            //sort
			            .queryParam("sort", "-" + treasuryExchangeRateFilter.getSortBy().name().toLowerCase())
			            //pagination
			            .queryParam("page[number]", paginationFilter.getPageNumber())
			            .queryParam("page[size]", paginationFilter.getPageSize())
			            .build())
                .retrieve()
                .bodyToMono(TreasuryExchangeRateResponseDto.class)
                .map(resp -> exchangeRateMapper.toDomainList(resp.getData()));*/
		
		
		Mono<List<ExchangeRate>> remoteCall = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(properties.getPath())
                        // filter
                        .queryParam("filter", String.format(
                                "effective_date:lte:%s,effective_date:gte:%s,country:eq:%s",
                                treasuryExchangeRateFilter.getRequestDateTo().format(TreasuryApiConstants.TREASURY_DATE_FORMATTER),
                                treasuryExchangeRateFilter.getRequestDateFrom().format(TreasuryApiConstants.TREASURY_DATE_FORMATTER),
                                StringUtils.capitalizeFirstLetter(treasuryExchangeRateFilter.getCountry())
                        ))
                        // sort
                        .queryParam("sort", "-" + treasuryExchangeRateFilter.getSortBy().name().toLowerCase())
                        // pagination
                        .queryParam("page[number]", paginationFilter.getPageNumber())
                        .queryParam("page[size]", paginationFilter.getPageSize())
                        .build())
                .retrieve()
                .bodyToMono(TreasuryExchangeRateResponseDto.class)
                .map(resp -> exchangeRateMapper.toDomainList(resp.getData()));

        
        return treasuryBreaker.run(
                remoteCall,
                throwable -> fallbackExchangeRates(treasuryExchangeRateFilter, throwable)
        );
                
		
	}
	
	private Mono<List<ExchangeRate>> fallbackExchangeRates(TreasuryExchangeRateFilterDto filter, Throwable t) {
        log.error("Fallback triggered for country {}: {}", filter.getCountry(), t.getMessage());
        return Mono.just(Collections.emptyList());
    }
	
	
	@Scheduled(fixedRate = 86400000)
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
                		
                log.info("CacheService class: {}", cacheService.getClass());
                if (exchangeRateList != null && !exchangeRateList.isEmpty()) {
                	cacheService.cacheExchangeRate(StringUtils.capitalizeFirstLetter(country), exchangeRateList);
                } else {
                	cacheService.cacheExchangeRate(StringUtils.capitalizeFirstLetter(country), new ArrayList<>());
                }	
                
            } catch (Exception e) {
                log.error("Exception fetching exchange rate for country {}: {}", country, e.getMessage());
            }
        }
    }
	

}
