package net.mycompany.commerce.purchasemgmt.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.mycompany.commerce.common.util.DateUtils;
import net.mycompany.commerce.common.util.StringUtils;
import net.mycompany.commerce.purchasemgmt.domain.model.Currency;
import net.mycompany.commerce.purchasemgmt.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchasemgmt.domain.port.ExchangeRateProviderPort;
import net.mycompany.commerce.purchasemgmt.domain.valueobject.ConvertedCurrency;
import net.mycompany.commerce.purchasemgmt.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.cache.CacheService;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.exception.ApiServiceUnavaliableException;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.exception.PurchaseDomainException;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.TreasuryExchangeRateSortDto;

@Service
public class PurchaseDomainService {
	
	private static final Logger log = LoggerFactory.getLogger(PurchaseDomainService.class);

	private final String notLastSixMonthsErrorCode;
	private final String notLastSixMonthsErrorMessage;
	private final String treasuryServiceErrorMessage;
	private final String currencyConversionParamNullErrorCode;
	private final String currencyConversionParamNullErrorMessage;
	private final ExchangeRateProviderPort exchangeRateProviderPort;
	private final CacheService cacheService;
	
	public PurchaseDomainService(
			ExchangeRateProviderPort providerPort,
			CacheService cacheService,
			@Value("${error.domain.exchange.notLastSixMonths.code}") String notLastSixMonthsErrorCode,
			@Value("${error.domain.exchange.notLastSixMonths.message}") String notLastSixMonthsErrorMessage,
			@Value("${error.domain.exchange.treasury.service.error}") String treasuryServiceErrorMessage,
			@Value("${error.domain.exchange.null-params.code}") String currencyConversionParamNullErrorCode,
			@Value("${error.domain.exchange.null-params.message}") String currencyConversionParamNullErrorMessage){
		this.exchangeRateProviderPort = providerPort;
		this.cacheService = cacheService;
		this.notLastSixMonthsErrorCode = notLastSixMonthsErrorCode;
		this.notLastSixMonthsErrorMessage = notLastSixMonthsErrorMessage;
		this.treasuryServiceErrorMessage = treasuryServiceErrorMessage;
		this.currencyConversionParamNullErrorCode = currencyConversionParamNullErrorCode;
		this.currencyConversionParamNullErrorMessage = currencyConversionParamNullErrorMessage;
	}
	

    public ConvertedCurrency currencyConversion(PurchaseTransaction purchaseTransaction, Currency currencyOut){
    	
    	
    	if(purchaseTransaction == null) {
			throw new PurchaseDomainException(currencyConversionParamNullErrorCode, currencyConversionParamNullErrorMessage);
		}
    	
    	if(currencyOut == null || currencyOut.getCountry() == null) {
    		throw new PurchaseDomainException(currencyConversionParamNullErrorCode, currencyConversionParamNullErrorMessage);
    	}
    	
    	List<ExchangeRate> exchangeRateList = null;
    	
    	if(DateUtils.isDateToday(purchaseTransaction.getPurchaseDate())) {
    		
    		log.debug("getting exchange rates from cache for country {}", currencyOut.getCountry());
    		//try on cache
    		exchangeRateList = cacheService.getCachedExchangeRateList(StringUtils.capitalizeFirstLetter(currencyOut.getCountry()));
    		
    		
    	}
    	
    	if(exchangeRateList == null) {
			//it's not a environment country or PurchaseDate is not today
			try {
				
				log.debug("fetching exchange rates from treasury service for country {}", currencyOut.getCountry());
				
				exchangeRateList = exchangeRateProviderPort.getTreasuryExchangeRateFromRestClient(
                        
                       TreasuryExchangeRateFilterDto.builder()
                        .country(currencyOut.getCountry())
                        .requestDateTo(purchaseTransaction.getPurchaseDate())
                        .requestDateFrom(DateUtils.getDateSixMonthsBack(purchaseTransaction.getPurchaseDate()))
                        .sortBy(TreasuryExchangeRateSortDto.EFFECTIVE_DATE)
                        .build()).block();
    			
				//there's no exchange rate within the last 6 months for this country or this country doesn't exist in treasury service
				if (exchangeRateList == null || exchangeRateList.isEmpty()) {
					throw new PurchaseDomainException(notLastSixMonthsErrorCode, notLastSixMonthsErrorMessage);
                } 
			
			} catch (PurchaseDomainException ex) {
		        throw ex;
			}catch(Exception e) {
				log.error("Exception fetching exchange rate for country {}: {}", currencyOut.getCountry(), e.getMessage());
				throw new ApiServiceUnavaliableException(treasuryServiceErrorMessage);
			}
		}	
    	
    	//is a environment country but has no rates within the last 6 months
		if(exchangeRateList.isEmpty()) {
			throw new PurchaseDomainException(notLastSixMonthsErrorCode, notLastSixMonthsErrorMessage);
		}
		
    	
    	log.debug("exchange rates found: {}", exchangeRateList.size());
    	
    	for (ExchangeRate exchangeRate : exchangeRateList) {
			
    		log.debug("checking exchange rate {} and effective date {} for purchase date {}", exchangeRate.getExchangeRateAmount(),exchangeRate.getEffectiveDate());	
    		//PurchaseDate must be grather then the effectiveDate from this element
    		if(purchaseTransaction.getPurchaseDate().isAfter(exchangeRate.getEffectiveDate()) 
    			||	purchaseTransaction.getPurchaseDate().isEqual(exchangeRate.getEffectiveDate())) {
    			
    			log.debug("using exchange rate with effective date {} for purchase date {}", exchangeRate.getEffectiveDate(), purchaseTransaction.getPurchaseDate());
    			
    			 return ConvertedCurrency.builder()
		    			.currency(currencyOut)
		    			.exchangeRateAmount(exchangeRate.getExchangeRateAmount())
		    			.convertedAmount(calculateCurrencyConversion(purchaseTransaction.getAmount(), exchangeRate.getExchangeRateAmount()))
		    			.build();
    		}
    		
		}
    	
    	//if PurchaseDate is lower then all the effectiveDate from the list
    	throw new PurchaseDomainException(notLastSixMonthsErrorCode, notLastSixMonthsErrorMessage);
    	
    }
    	
    	
    	
    	
    			
    	public BigDecimal calculateCurrencyConversion(BigDecimal originalAmount, BigDecimal exchangeRate) {
    		
    		
            if (originalAmount == null) {
            	throw new PurchaseDomainException(currencyConversionParamNullErrorCode, currencyConversionParamNullErrorMessage);
            }
            
            if (exchangeRate == null) {
            	throw new PurchaseDomainException(currencyConversionParamNullErrorCode, currencyConversionParamNullErrorMessage);
            }
            
            
            
            return originalAmount
                    .multiply(exchangeRate)
                    .setScale(2, RoundingMode.HALF_UP);
        				
    	}
    

    

}