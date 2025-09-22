package net.mycompany.commerce.purchase.application.exchange.controller;

import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateRequestDto;
import net.mycompany.commerce.purchase.application.exchange.dto.ExchangeRateResponseDto;
import net.mycompany.commerce.purchase.application.exchange.service.CurrencyExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExchangeControllerTest {
    private CurrencyExchangeService currencyExchangeService;
    private ExchangeController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        currencyExchangeService = mock(CurrencyExchangeService.class);
        controller = new ExchangeController(currencyExchangeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testConvertCurrencySuccess() throws Exception {
        ExchangeRateRequestDto request = ExchangeRateRequestDto.builder()
                .transactionId("tx-1")
                .countryName("Brazil")
                .build();
        ExchangeRateResponseDto response = ExchangeRateResponseDto.builder()
                .transactionId("tx-1")
                .targetAmount(java.math.BigDecimal.TEN)
                .exchangeRate(java.math.BigDecimal.ONE)
                .build();
        when(currencyExchangeService.convertCurrency(any())).thenReturn(response);
        mockMvc.perform(post("/purchase/exchange/v1/convertCurrency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("tx-1"))
                .andExpect(jsonPath("$.targetAmount").value(10))
                .andExpect(jsonPath("$.exchangeRate").value(1));
    }

    @Test
    void testConvertCurrencyThrows() throws Exception {
        ExchangeRateRequestDto request = ExchangeRateRequestDto.builder()
                .transactionId("tx-1")
                .countryName("Brazil")
                .build();
        when(currencyExchangeService.convertCurrency(any())).thenThrow(new RuntimeException("Service error"));
        assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/purchase/exchange/v1/convertCurrency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        });
    }
}