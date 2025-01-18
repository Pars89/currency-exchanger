package com.timerg.mapper;

import com.timerg.dto.CreateExchangeRateDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.entity.ExchangeRatesEntity;

import java.math.BigDecimal;

public class CreateExchangeRateToEntityMapper implements Mapper<CreateExchangeRateDto, ExchangeRatesEntity> {
    private static final CreateExchangeRateToEntityMapper INSTANCE = new CreateExchangeRateToEntityMapper();
    private final ReadCurrencyToEntityMapper readCurrencyToEntityMapper = ReadCurrencyToEntityMapper.getInstance();

    private CreateExchangeRateToEntityMapper() {
    }

    @Override
    public ExchangeRatesEntity from(CreateExchangeRateDto createExchangeRateDto) {
        throw new UnsupportedOperationException();
    }

    public ExchangeRatesEntity from(CreateExchangeRateDto createExchangeRateDto, ReadCurrencyDto readBaseCurrencyDto, ReadCurrencyDto readTargetCurrencyDto) {
        return ExchangeRatesEntity.builder()
                .baseCurrencyId(readCurrencyToEntityMapper.from(readBaseCurrencyDto))
                .targetCurrencyId(readCurrencyToEntityMapper.from(readTargetCurrencyDto))
                .rate(new BigDecimal(createExchangeRateDto.getRate()))
                .build();
    }

    public static CreateExchangeRateToEntityMapper getInstance() {
        return INSTANCE;
    }
}
