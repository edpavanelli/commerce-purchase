package net.mycompany.commerce.purchase.application.exchange.controller;

import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateRequestDto;
import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateResponseDto;
import net.mycompany.commerce.purchase.application.exchange.service.CurrencyExchangeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Tag(name = "Currency Exchange", description = "Endpoints for currency conversion and exchange rates.")
@RestController
@RequestMapping(path = "/purchase/exchange/v1")
public class ExchangeController {
	private static final Logger log = LoggerFactory.getLogger(ExchangeController.class);
    private final CurrencyExchangeService currencyExchangeService;

    public ExchangeController(CurrencyExchangeService currencyExchangeService) {
        this.currencyExchangeService = currencyExchangeService;
    }

    @Operation(
        summary = "Convert currency for a purchase transaction",
        description = "Converts the purchase amount to the target country's currency using the latest exchange rate.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Conversion successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
        }
    )
    
    @PostMapping(path = "/convertCurrency", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRateResponseDto> convertCurrency(@Valid @RequestBody ExchangeRateRequestDto request) {
        log.debug("Received currency conversion request: {}", request);
        ExchangeRateResponseDto response = currencyExchangeService.convertCurrency(request);
        log.debug("Processed currency conversion request: {}", response);
        return ResponseEntity.ok(response);
    }
}