package com.timerg.service;

import com.timerg.dao.CurrencyDao;
import com.timerg.dto.CurrencyDto;
import com.timerg.entity.CurrencyEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }


    public List<CurrencyDto> findAll() {

        return currencyDao.findAll().stream()
                .map(currencyEntity -> CurrencyDto.builder()
                        .id(currencyEntity.getId())
                        .code(currencyEntity.getCode())
                        .fullName(currencyEntity.getFullName())
                        .sign(currencyEntity.getSign())
                        .build()
                ).collect(Collectors.toList());

    }

    public Integer create(CurrencyEntity currencyEntity) {
        //validation


        // mapping


        //save

        // return

        return 1;
    }

    public Optional<CurrencyDto> findByCode(String code) {
        return currencyDao.findByCode(code)
                .map(currencyEntity -> CurrencyDto.builder()
                        .id(currencyEntity.getId())
                        .code(currencyEntity.getCode())
                        .fullName(currencyEntity.getFullName())
                        .sign(currencyEntity.getSign())
                        .build()
                );
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }


}
