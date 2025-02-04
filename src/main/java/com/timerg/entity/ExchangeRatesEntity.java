package com.timerg.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRatesEntity {
    private Integer id;
    private CurrencyEntity baseCurrencyId;
    private CurrencyEntity targetCurrencyId;
    private BigDecimal rate;
}
