package com.timerg.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CreateExchangeRateDto {
    String baseCurrencyId;
    String targetCurrencyId;
    String rate;
}
