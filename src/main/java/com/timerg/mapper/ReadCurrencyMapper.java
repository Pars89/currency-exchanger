package com.timerg.mapper;

import com.timerg.dto.ReadCurrencyDto;
import com.timerg.entity.CurrencyEntity;

public class ReadCurrencyMapper implements Mapper<CurrencyEntity, ReadCurrencyDto>{
    private static final ReadCurrencyMapper INSTANCE = new ReadCurrencyMapper();

    private ReadCurrencyMapper(){
    }

    @Override
    public ReadCurrencyDto from(CurrencyEntity currencyEntity) {

        return ReadCurrencyDto.builder()
                .id(currencyEntity.getId())
                .fullName(currencyEntity.getFullName())
                .code(currencyEntity.getCode())
                .sign(currencyEntity.getSign())
                .build();
    }

    public static ReadCurrencyMapper getInstance() {
        return INSTANCE;
    }
}
