package net.mycompany.commerce.purchasemgmt.application.exchange.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.mycompany.commerce.purchasemgmt.application.exchange.controller.ExchangeController;
import net.mycompany.commerce.purchasemgmt.application.exchange.dto.ExchangeRateRequestDto;
import net.mycompany.commerce.purchasemgmt.application.exchange.service.CurrencyExchangeService;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class ExchangeControllerTest {
    private CurrencyExchangeService currencyExchangeService;
    private ExchangeController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        currencyExchangeService = mock(CurrencyExchangeService.class);
        controller = new ExchangeController(currencyExchangeService);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setValidator(new LocalValidatorFactoryBean())
            .setMessageConverters(converter)
            .build();
    }

    @Test
    void testConvertCurrencyThrows() throws Exception {
        ExchangeRateRequestDto invalidRequest = new ExchangeRateRequestDto(null, null);
        mockMvc.perform(post("/purchase/exchange/v1/convertCurrency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testConvertCurrencySuccess() throws Exception {
        
        ExchangeRateRequestDto validRequest = new ExchangeRateRequestDto("TX123456789", "Brazil");

        net.mycompany.commerce.common.dto.CurrencyDto purchaseCurrencyDto = net.mycompany.commerce.common.dto.CurrencyDto.builder()
                .code("USD")
                .name("US Dollar")
                .country("United States")
                .build();

        net.mycompany.commerce.purchasemgmt.application.exchange.dto.ExchangeRateResponseDto responseDto = net.mycompany.commerce.purchasemgmt.application.exchange.dto.ExchangeRateResponseDto.builder()
                .transactionId("TX123456789")
                .description("Purchase of electronics")
                .purchaseCurrency(purchaseCurrencyDto)
                .purchaseAmount(new java.math.BigDecimal("100.00"))
                .transactionDate(java.time.LocalDate.of(2025, 9, 22))
                .targetCountry("Brazil")
                .targetAmount(new java.math.BigDecimal("500.00"))
                .exchangeRate(new java.math.BigDecimal("5.00"))
                .build();

        when(currencyExchangeService.convertCurrency(any())).thenReturn(responseDto);

        mockMvc.perform(post("/purchase/exchange/v1/convertCurrency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TX123456789"))
                .andExpect(jsonPath("$.description").value("Purchase of electronics"))
                .andExpect(jsonPath("$.purchaseCurrency.code").value("USD"))
                .andExpect(jsonPath("$.purchaseCurrency.name").value("US Dollar"))
                .andExpect(jsonPath("$.purchaseCurrency.country").value("United States"))
                .andExpect(jsonPath("$.purchaseAmount").value(100.00))
                .andExpect(jsonPath("$.transactionDate").value("2025-09-22"))
                .andExpect(jsonPath("$.targetCountry").value("Brazil"))
                .andExpect(jsonPath("$.targetAmount").value(500.00))
                .andExpect(jsonPath("$.exchangeRate").value(5.00));
    }
}