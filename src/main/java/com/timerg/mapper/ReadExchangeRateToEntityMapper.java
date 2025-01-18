package com.timerg.mapper;

import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.entity.ExchangeRatesEntity;


public class ReadExchangeRateToEntityMapper implements Mapper<ReadExchangeRateDto, ExchangeRatesEntity> {
    private static final ReadExchangeRateToEntityMapper INSTANCE = new ReadExchangeRateToEntityMapper();
    private final ReadCurrencyToEntityMapper readCurrencyToEntityMapper = ReadCurrencyToEntityMapper.getInstance();

    private ReadExchangeRateToEntityMapper() {
    }

    @Override
    public ExchangeRatesEntity from(ReadExchangeRateDto readExchangeRateDto) {
        return ExchangeRatesEntity.builder()
                .id(readExchangeRateDto.getId())
                .baseCurrencyId(readCurrencyToEntityMapper.from(readExchangeRateDto.getBaseCurrencyId()))
                .targetCurrencyId(readCurrencyToEntityMapper.from(readExchangeRateDto.getTargetCurrencyId()))
                .rate(readExchangeRateDto.getRate())
                .build();
    }

    public static ReadExchangeRateToEntityMapper getInstance() {
        return INSTANCE;
    }
}
