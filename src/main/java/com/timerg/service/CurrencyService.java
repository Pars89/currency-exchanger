package com.timerg.service;

import com.timerg.dao.CurrencyDao;
import com.timerg.dto.CreateCurrencyDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.entity.CurrencyEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }


    public List<ReadCurrencyDto> findAll() {

        return currencyDao.findAll().stream()
                .map(currencyEntity -> ReadCurrencyDto.builder()
                        .id(currencyEntity.getId())
                        .code(currencyEntity.getCode())
                        .fullName(currencyEntity.getFullName())
                        .sign(currencyEntity.getSign())
                        .build()
                ).collect(Collectors.toList());

    }

    public ReadCurrencyDto create(CreateCurrencyDto createCurrencyDto) {
        //validation


        // mapping
        CurrencyEntity currencyEntity = CurrencyEntity.builder()
                .fullName(createCurrencyDto.getFullName())
                .code(createCurrencyDto.getCode())
                .sign(createCurrencyDto.getSign())
                .build();


        //save
        CurrencyEntity saveCurrencyEntity = currencyDao.save(currencyEntity);

        // mapping
        ReadCurrencyDto readCurrencyDto = ReadCurrencyDto.builder()
                .id(saveCurrencyEntity.getId())
                .fullName(saveCurrencyEntity.getFullName())
                .code(saveCurrencyEntity.getCode())
                .sign(saveCurrencyEntity.getSign())
                .build();

        // return
        return readCurrencyDto;
    }

    public Optional<ReadCurrencyDto> findByCode(String code) {
        return currencyDao.findByCode(code)
                .map(currencyEntity -> ReadCurrencyDto.builder()
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
