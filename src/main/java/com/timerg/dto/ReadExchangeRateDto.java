package com.timerg.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ReadExchangeRateDto {
    Integer id;
    ReadCurrencyDto baseCurrencyId;
    ReadCurrencyDto targetCurrencyId;
    BigDecimal rate;
}
