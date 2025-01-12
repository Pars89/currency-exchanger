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
    private Integer BaseCurrencyId;
    private Integer TargetCurrencyId;
    private BigDecimal Rate;
}
