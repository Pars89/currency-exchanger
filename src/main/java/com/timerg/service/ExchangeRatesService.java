package com.timerg.service;

import com.timerg.dao.ExchangeRatesDao;
import com.timerg.dto.CreateExchangeRateDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.exception.CurrencyNotFoundException;
import com.timerg.exception.ExchangeRateAlreadyExistException;
import com.timerg.exception.ValidationException;
import com.timerg.mapper.CreateExchangeRateToEntityMapper;
import com.timerg.mapper.ExchangeRateEntityToReadMapper;
import com.timerg.validation.CreateExchangeRateValidator;
import com.timerg.validation.ValidationResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExchangeRatesService {
    private static final ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ExchangeRateEntityToReadMapper exchangeRateEntityToReadMapper = ExchangeRateEntityToReadMapper.getInstance();
    private final CreateExchangeRateToEntityMapper createExchangeRateToEntityMapper = CreateExchangeRateToEntityMapper.getInstance();
    private final CreateExchangeRateValidator createExchangeRateValidator = CreateExchangeRateValidator.getInstance();

    private ExchangeRatesService() {
    }

    public List<ReadExchangeRateDto> findAll() {
        return exchangeRatesDao.findAll().stream()
                .map(exchangeRateEntityToReadMapper::from)
                .collect(Collectors.toList());
    }

    public ReadExchangeRateDto findByCodes(String baseCode, String targetCode) {

        // get 1 id and 2 id from currencies by code
        var readBaseCurrencyDto = getReadCurrencyDtoByCodeOrThrow(baseCode);
        var readTargetCurrencyDto = getReadCurrencyDtoByCodeOrThrow(targetCode);

        // get exchangeRate
        return exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(readBaseCurrencyDto.getId(), readTargetCurrencyDto.getId())
                .map(exchangeRateEntityToReadMapper::from)
                .orElseThrow(() -> new CurrencyNotFoundException("Курс для валютной пары не найден: " + baseCode + " -> " + targetCode));
    }

    public ReadExchangeRateDto create(CreateExchangeRateDto createExchangeRateDto) {
        // validation
        validateExchangeRateDto(createExchangeRateDto);

        // get ReadCurrencyDto from currencies by code
        var readBaseCurrencyDto = getReadCurrencyDtoByCodeOrThrow(createExchangeRateDto.getBaseCurrencyId());
        var readTargetCurrencyDto = getReadCurrencyDtoByCodeOrThrow(createExchangeRateDto.getTargetCurrencyId());

        checkDuplicateExchangeRate(readBaseCurrencyDto.getId(), readTargetCurrencyDto.getId());

        // mapping
        var exchangeRatesEntity = createExchangeRateToEntityMapper.from(
                createExchangeRateDto,
                readBaseCurrencyDto,
                readTargetCurrencyDto
        );

        // save
        var savedExchangeRatesEntity = exchangeRatesDao.save(exchangeRatesEntity);

        // mapping and return
        return exchangeRateEntityToReadMapper.from(savedExchangeRatesEntity);
    }

    private void checkDuplicateExchangeRate(Integer baseCurrencyId, Integer targetCurrencyId) {
        if (exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, targetCurrencyId).isPresent()) {
            throw new ExchangeRateAlreadyExistException("Курс для этой валютной пары уже существует");
        }
    }

    public ReadExchangeRateDto updateByCodes(CreateExchangeRateDto createExchangeRateDto) {

        validateExchangeRateDto(createExchangeRateDto);

        var readBaseCurrencyDto = getReadCurrencyDtoByCodeOrThrow(createExchangeRateDto.getBaseCurrencyId());
        var readTargetCurrencyDto = getReadCurrencyDtoByCodeOrThrow(createExchangeRateDto.getTargetCurrencyId());

        boolean updated = exchangeRatesDao.updateByBaseCurrencyIdAndTargetCurrencyId(
                readBaseCurrencyDto.getId(),
                readTargetCurrencyDto.getId(),
                new BigDecimal(createExchangeRateDto.getRate())
        );

        // save
        if (!updated) {
            throw new CurrencyNotFoundException("Курс для валютной пары не найден: " +
                                                createExchangeRateDto.getBaseCurrencyId() + " -> " +
                                                createExchangeRateDto.getTargetCurrencyId());
        }

        return exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(readBaseCurrencyDto.getId(), readTargetCurrencyDto.getId())
                .map(exchangeRateEntityToReadMapper::from)
                .orElseThrow(() -> new CurrencyNotFoundException("Не удалось найти обновлённый курс валют"));
    }

    private void validateExchangeRateDto(CreateExchangeRateDto createExchangeRateDto) {
        ValidationResult validationResult = createExchangeRateValidator.isValid(createExchangeRateDto);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrorResponses());
        }
    }

    private Optional<ReadCurrencyDto> getCurrencyDtoByCode(String code){
        return Optional.ofNullable(currencyService.findByCode(code));
    }
    private ReadCurrencyDto getReadCurrencyDtoByCodeOrThrow(String code) {
        return Optional.ofNullable(currencyService.findByCode(code))
                .orElseThrow(() -> new CurrencyNotFoundException("Валюта с кодом " + code + " не найдена"));
    }

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }
}
