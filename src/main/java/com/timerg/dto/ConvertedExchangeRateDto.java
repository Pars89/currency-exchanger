package com.timerg.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ConvertedExchangeRateDto {
    ReadCurrencyDto baseCurrencyId;
    ReadCurrencyDto targetCurrencyId;
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal convertedAmount;
}
