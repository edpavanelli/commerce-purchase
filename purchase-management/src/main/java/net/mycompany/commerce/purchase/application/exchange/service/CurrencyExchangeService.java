package net.mycompany.commerce.purchase.application.exchange.service;

import net.mycompany.commerce.purchase.application.exchange.controller.ExchangeController;
import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateRequestDto;
import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateResponseDto;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.valueobject.ConvertedCurrency;
import net.mycompany.commerce.purchase.domain.valueobject.TransactionId;
import net.mycompany.commerce.purchase.infrastructure.repository.PurchaseTransactionRepository;
import net.mycompany.commerce.purchase.domain.service.PurchaseDomainService;
import net.mycompany.commerce.purchase.infrastructure.config.exception.DataBaseNotFoundException;
import net.mycompany.commerce.common.dto.CurrencyDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CurrencyExchangeService {

	private static final Logger log = LoggerFactory.getLogger(CurrencyExchangeService.class);
	
    private final PurchaseTransactionRepository purchaseTransactionRepository;
    private final PurchaseDomainService purchaseDomainService;
    private final String dataBaseNotFoundMessage;

    public CurrencyExchangeService(PurchaseTransactionRepository purchaseTransactionRepository,
                                   PurchaseDomainService purchaseDomainService,
                                   @Value("${error.database.notfound.message}") String dataBaseNotFoundMessage) {
        this.purchaseTransactionRepository = purchaseTransactionRepository;
        this.purchaseDomainService = purchaseDomainService;
        this.dataBaseNotFoundMessage = dataBaseNotFoundMessage;
    }

    public ExchangeRateResponseDto convertCurrency(ExchangeRateRequestDto dto) {
    	
        TransactionId transactionId = new TransactionId(dto.getTransactionId());
        
        log.debug("Looking for purchaseTransaction: {}", transactionId);
        
        Optional<PurchaseTransaction> optionalTransaction = purchaseTransactionRepository.findByTransactionId(transactionId);
        
        if (optionalTransaction.isEmpty()) {
            throw new DataBaseNotFoundException(dataBaseNotFoundMessage);
        }
        
        PurchaseTransaction purchaseTransaction = optionalTransaction.get();
        
        log.debug("Found purchaseTransaction: {}", purchaseTransaction);

        Currency targetCurrency = Currency.builder()
                .country(dto.getCountryName())
                .build();

        
        log.debug("Converting currency to targetCurrency: {}", targetCurrency);
        
        // Get exchanged amount
        ConvertedCurrency convertedCurrency = purchaseDomainService.currencyConversion(purchaseTransaction, targetCurrency);

        log.debug("currency converted: {}", convertedCurrency);
        log.debug("building response");
        
        // Map purchaseCurrency
        Currency purchaseCurrency = purchaseTransaction.getCurrency();
        CurrencyDto purchaseCurrencyDto = CurrencyDto.builder()
                .code(purchaseCurrency.getCode())
                .name(purchaseCurrency.getName())
                .country(purchaseCurrency.getCountry())
                .build();

        // Map targetCurrency
        CurrencyDto targetCurrencyDto = CurrencyDto.builder()
                .country(dto.getCountryName())
                .build();


        return ExchangeRateResponseDto.builder()
                .transactionId(dto.getTransactionId())
                .description(purchaseTransaction.getDescription())
                .purchaseCurrency(purchaseCurrencyDto)
                .purchaseAmount(purchaseTransaction.getAmount())
                .transactionDate(purchaseTransaction.getPurchaseDate())
                .targetCurrency(targetCurrencyDto)
                .targetAmount(convertedCurrency.getConvertedAmount())
                .exchangeRate(convertedCurrency.getExchangeRateAmount())
                .build();
    }
}