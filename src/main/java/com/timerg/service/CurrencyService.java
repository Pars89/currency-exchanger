package com.timerg.service;

import com.timerg.dao.CurrencyDao;
import com.timerg.dto.CreateCurrencyDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.entity.CurrencyEntity;
import com.timerg.exception.*;
import com.timerg.mapper.CreateCurrencyToEntityMapper;
import com.timerg.mapper.CurrencyEntityToReadMapper;
import com.timerg.validation.CodeCurrencyValidator;
import com.timerg.validation.CreateCurrencyValidator;
import com.timerg.validation.ValidationResult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final CurrencyEntityToReadMapper currencyEntityToReadMapper = CurrencyEntityToReadMapper.getInstance();
    private final CreateCurrencyToEntityMapper createCurrencyToEntityMapper = CreateCurrencyToEntityMapper.getInstance();
    private final CreateCurrencyValidator createCurrencyValidator = CreateCurrencyValidator.getInstance();
    private final CodeCurrencyValidator codeCurrencyValidator = CodeCurrencyValidator.getInstance();

    private CurrencyService() {
    }

    public List<ReadCurrencyDto> findAll() {
        return currencyDao.findAll().stream()
                .map(currencyEntityToReadMapper::from)
                .collect(Collectors.toList());
    }

    public ReadCurrencyDto create(CreateCurrencyDto createCurrencyDto) {
        //validation
        validateCurrency(createCurrencyDto);
        // mapping
        CurrencyEntity currencyEntity = createCurrencyToEntityMapper.from(createCurrencyDto);
        //save
        CurrencyEntity savedCurrencyEntity = currencyDao.save(currencyEntity);
        // mapping
        ReadCurrencyDto readCurrencyDto = currencyEntityToReadMapper.from(savedCurrencyEntity);

        return readCurrencyDto;
    }

    private void validateCurrency(CreateCurrencyDto createCurrencyDto) {
        ValidationResult validationResult = createCurrencyValidator.isValid(createCurrencyDto);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrorResponses());
        }

        if (existByCode(createCurrencyDto.getCode())) {
            throw new CurrencyAlreadyExistsException("Валюта с кодом '" + createCurrencyDto.getCode() + "' уже существует");
        }
    }

    public ReadCurrencyDto findByCode(String code) {
        ValidationResult validationResult = codeCurrencyValidator.isValid(code);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrorResponses());
        }

        Optional<ReadCurrencyDto> readCurrencyDto = currencyDao.findByCode(code)
                .map(currencyEntityToReadMapper::from);

        if (readCurrencyDto.isPresent()) {
            return readCurrencyDto.get();
        } else {
            throw new CurrencyNotFoundException("Валюта с кодом '" + code + "' не найдена");
        }
    }

    public boolean existByCode(String code) {
        return currencyDao.existByCode(code);
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

}
