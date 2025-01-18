package com.timerg.mapper;

import com.timerg.dto.ReadCurrencyDto;
import com.timerg.entity.CurrencyEntity;

public class ReadCurrencyToEntityMapper implements Mapper<ReadCurrencyDto, CurrencyEntity>{

    private static final ReadCurrencyToEntityMapper INSTANCE = new ReadCurrencyToEntityMapper();

    private ReadCurrencyToEntityMapper(){
    }

    @Override
    public CurrencyEntity from(ReadCurrencyDto readCurrencyDto) {
        return CurrencyEntity.builder()
                .id(readCurrencyDto.getId())
                .fullName(readCurrencyDto.getFullName())
                .code(readCurrencyDto.getCode())
                .sign(readCurrencyDto.getSign())
                .build();
    }

    public static ReadCurrencyToEntityMapper getInstance() {
        return INSTANCE;
    }
}
