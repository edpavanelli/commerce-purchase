package net.mycompany.commerce.purchase.application.startup;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import net.mycompany.commerce.purchase.domain.port.ExchangeRateProviderPort;
import net.mycompany.commerce.purchase.domain.service.PurchaseDomainService;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateSortDto;
import net.mycompany.commerce.common.dto.PaginationFiltersDto;

@Component
public class TreasuryExchangeRateInitializer {
    private static final Logger log = LoggerFactory.getLogger(TreasuryExchangeRateInitializer.class);

    @Value("${environment.default.cuntries.exchange}")
    private String countriesProperty;

    private final ExchangeRateProviderPort exchangeRateProvider;

    public TreasuryExchangeRateInitializer(ExchangeRateProviderPort exchangeRateProvider) {
        this.exchangeRateProvider = exchangeRateProvider;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        List<String> countries = Arrays.asList(countriesProperty.split(","));
        for (String country : countries) {
            try {
                TreasuryExchangeRateFilterDto filter = TreasuryExchangeRateFilterDto.builder()
                    .country(country)
                    .requestDateTo(LocalDate.now().atStartOfDay())
                    .requestDateFrom(PurchaseDomainService.getDateSixMonthsBack())
                    .build();
                PaginationFiltersDto pagination = PaginationFiltersDto.builder()
                    .pageNumber(1)
                    .pageSize(1)
                    .build();
                exchangeRateProvider.getTreasuryExchangeRate(
                        filter,
                        TreasuryExchangeRateSortDto.EFFECTIVE_DATE,
                        pagination
                ).subscribe(
                    exchangeRates -> {
                        if (exchangeRates != null && !exchangeRates.isEmpty() && exchangeRates.get(0) != null) {
                            cacheExchangeRate(country, exchangeRates.get(0).getExchangeRateAmount(), exchangeRates.get(0).getEffectiveDate());
                        } else {
                            cacheExchangeRate(country, null, null);
                        }
                    },
                    error -> log.error("Error fetching exchange rate for country {}: {}", country, error.getMessage())
                );
            } catch (Exception e) {
                log.error("Exception fetching exchange rate for country {}: {}", country, e.getMessage());
            }
        }
    }

    @CachePut(value = "treasuryExchangeRateCache", key = "#country")
    public void cacheExchangeRate(String country, BigDecimal exchangeRate, LocalDate effectiveDate) {
        // The cache will store a simple object or map with country, exchangeRate, effectiveDate
        log.info("Cached exchange rate for {}: rate={}, date={}", country, exchangeRate, effectiveDate);
    }
}