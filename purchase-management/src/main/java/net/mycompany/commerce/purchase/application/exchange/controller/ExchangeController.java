package net.mycompany.commerce.purchase.application.exchange.controller;

import net.mycompany.commerce.mock.ProducerMock;
import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateRequestDto;
import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateResponseDto;
import net.mycompany.commerce.purchase.application.exchange.service.CurrencyExchangeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/purchase/exchange/v1/")
public class ExchangeController {
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeController.class);

    private final CurrencyExchangeService currencyExchangeService;

    public ExchangeController(CurrencyExchangeService currencyExchangeService) {
        this.currencyExchangeService = currencyExchangeService;
    }

    @PostMapping(path = "/convertCurrency", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRateResponseDto> convertCurrency(@Validated @RequestBody ExchangeRateRequestDto request) {
        
    	log.debug("Received currency conversion request: {}", request);
    	
    	ExchangeRateResponseDto response = currencyExchangeService.convertCurrency(request);
        
    	log.debug("processed currency conversion request: {}", response);
    	
    	return ResponseEntity.ok(response);
    }
}