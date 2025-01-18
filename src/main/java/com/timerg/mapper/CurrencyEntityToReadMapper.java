package com.timerg.mapper;

import com.timerg.dto.ReadCurrencyDto;
import com.timerg.entity.CurrencyEntity;

public class CurrencyEntityToReadMapper implements Mapper<CurrencyEntity, ReadCurrencyDto>{
    private static final CurrencyEntityToReadMapper INSTANCE = new CurrencyEntityToReadMapper();

    private CurrencyEntityToReadMapper(){
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

    public static CurrencyEntityToReadMapper getInstance() {
        return INSTANCE;
    }
}
