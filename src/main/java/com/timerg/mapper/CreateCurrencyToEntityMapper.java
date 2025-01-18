package com.timerg.mapper;

import com.timerg.dto.CreateCurrencyDto;
import com.timerg.entity.CurrencyEntity;

public class CreateCurrencyToEntityMapper implements Mapper<CreateCurrencyDto, CurrencyEntity> {

    private static final CreateCurrencyToEntityMapper INSTANCE = new CreateCurrencyToEntityMapper();
    private CreateCurrencyToEntityMapper() {
    }

    @Override
    public CurrencyEntity from(CreateCurrencyDto createCurrencyDto) {
        return CurrencyEntity.builder()
                .fullName(createCurrencyDto.getFullName())
                .code(createCurrencyDto.getCode())
                .sign(createCurrencyDto.getSign())
                .build();
    }

    public static CreateCurrencyToEntityMapper getInstance() {
        return INSTANCE;
    }
}
