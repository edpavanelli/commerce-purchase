package net.mycompany.commerce.purchase.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.cache.Cache;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.port.ExchangeRateProviderPort;
import net.mycompany.commerce.purchase.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateSortDto;
import net.mycompany.commerce.common.dto.PaginationFiltersDto;

public class PurchaseDomainService {

    public static LocalDateTime getDateSixMonthsBack() {
        return LocalDate.now().minusMonths(6).atStartOfDay();
    }

    public static BigDecimal currencyConversion(
            PurchaseTransaction purchaseTransaction,
            Currency currencyOut,
            BigDecimal purchaseAmountOut,
            ExchangeRateProviderPort exchangeRateProvider,
            Cache cache
    ) {
        LocalDateTime purchaseDate = purchaseTransaction.getPurchaseDate();
        LocalDateTime sixMonthsBack = getDateSixMonthsBack();
        LocalDateTime now = LocalDate.now().atStartOfDay();
        String country = currencyOut.getCountry();

        // Check if purchaseDate is within last 6 months
        boolean isWithinSixMonths = !purchaseDate.isBefore(sixMonthsBack) && !purchaseDate.isAfter(now);
        if (isWithinSixMonths) {
            Cache.ValueWrapper wrapper = cache.get(country);
            if (wrapper != null && wrapper.get() != null) {
                Object cachedValue = wrapper.get();
                List<ExchangeRate> exchangeRates;
                if (cachedValue instanceof List) {
                    exchangeRates = (List<ExchangeRate>) cachedValue;
                } else if (cachedValue instanceof ExchangeRate) {
                    exchangeRates = List.of((ExchangeRate) cachedValue);
                } else {
                    throw new RuntimeException("the purchase cannot be converted to the target currency");
                }
                ExchangeRate found = null;
                for (ExchangeRate rate : exchangeRates) {
                    if (rate != null && !purchaseDate.toLocalDate().isBefore(rate.getEffectiveDate())) {
                        found = rate;
                        break;
                    }
                }
                if (found != null && found.getExchangeRateAmount() != null) {
                    return purchaseTransaction.getAmount().multiply(found.getExchangeRateAmount());
                } else {
                    throw new RuntimeException("the purchase cannot be converted to the target currency");
                }
            } else {
                throw new RuntimeException("the purchase cannot be converted to the target currency");
            }
        } else {
            // Not in cache or not in last 6 months, call provider
            TreasuryExchangeRateFilterDto filter = TreasuryExchangeRateFilterDto.builder()
                    .country(country)
                    .requestDateFrom(getDateSixMonthsBack(purchaseDate))
                    .requestDateTo(purchaseDate)
                    .build();
            PaginationFiltersDto pagination = PaginationFiltersDto.builder()
                    .pageNumber(1)
                    .pageSize(10)
                    .build();
            List<ExchangeRate> exchangeRates = exchangeRateProvider.getTreasuryExchangeRate(
                    filter,
                    TreasuryExchangeRateSortDto.EFFECTIVE_DATE,
                    pagination
            ).block();
            if (exchangeRates == null || exchangeRates.isEmpty()) {
                throw new RuntimeException("the purchase cannot be converted to the target currency");
            }
            ExchangeRate found = null;
            for (ExchangeRate rate : exchangeRates) {
                if (rate != null && !purchaseDate.toLocalDate().isBefore(rate.getEffectiveDate())) {
                    found = rate;
                    break;
                }
            }
            if (found != null && found.getExchangeRateAmount() != null) {
                return purchaseTransaction.getAmount().multiply(found.getExchangeRateAmount());
            } else {
                throw new RuntimeException("the purchase cannot be converted to the target currency");
            }
        }
    }

    private static LocalDateTime getDateSixMonthsBack(LocalDateTime referenceDate) {
        return referenceDate.toLocalDate().minusMonths(6).atStartOfDay();
    }

}