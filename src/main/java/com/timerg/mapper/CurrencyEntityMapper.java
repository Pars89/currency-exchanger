package com.timerg.mapper;

import com.timerg.dto.ReadCurrencyDto;
import com.timerg.entity.CurrencyEntity;

public class CurrencyEntityMapper implements Mapper<ReadCurrencyDto, CurrencyEntity>{

    private static final CurrencyEntityMapper INSTANCE = new CurrencyEntityMapper();

    private CurrencyEntityMapper(){
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

    public static CurrencyEntityMapper getInstance() {
        return INSTANCE;
    }
}
