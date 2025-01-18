package com.timerg.mapper;

import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.entity.ExchangeRatesEntity;

public class ExchangeRateEntityToReadMapper implements Mapper<ExchangeRatesEntity, ReadExchangeRateDto> {
    private static final ExchangeRateEntityToReadMapper INSTANCE = new ExchangeRateEntityToReadMapper();
    private final CurrencyEntityToReadMapper currencyEntityToReadMapper = CurrencyEntityToReadMapper.getInstance();

    private ExchangeRateEntityToReadMapper() {
    }

    @Override
    public ReadExchangeRateDto from(ExchangeRatesEntity exchangeRatesEntity) {
        return ReadExchangeRateDto.builder()
                .id(exchangeRatesEntity.getId())
                .baseCurrencyId(currencyEntityToReadMapper.from(exchangeRatesEntity.getBaseCurrencyId()))
                .targetCurrencyId(currencyEntityToReadMapper.from(exchangeRatesEntity.getTargetCurrencyId()))
                .rate(exchangeRatesEntity.getRate())
                .build();
    }

    public static ExchangeRateEntityToReadMapper getInstance() {
        return INSTANCE;
    }
}
