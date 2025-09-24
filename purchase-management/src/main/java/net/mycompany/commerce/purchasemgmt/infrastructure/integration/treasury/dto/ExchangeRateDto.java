package net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateDto {
    @JsonProperty("country")
    private String country;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("exchange_rate")
    private String exchangeRateAmount;
    @JsonProperty("effective_date")
    private String effectiveDate;
}