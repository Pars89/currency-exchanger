package com.timerg.service;

import com.timerg.dao.ExchangeRatesDao;
import com.timerg.dto.CreateExchangeRateDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.entity.ExchangeRatesEntity;
import com.timerg.mapper.CreateExchangeRateToEntityMapper;
import com.timerg.mapper.CurrencyEntityToReadMapper;
import com.timerg.mapper.ExchangeRateEntityToReadMapper;
import com.timerg.mapper.ReadCurrencyToEntityMapper;
import com.timerg.util.RateFormat;

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

    private ExchangeRatesService() {
    }

    public List<ReadExchangeRateDto> findAll() {
        return exchangeRatesDao.findAll().stream()
                .map(exchangeRateEntityToReadMapper::from)
                .collect(Collectors.toList());
    }

    public Optional<ReadExchangeRateDto> findByCodes(String baseCode, String targetCode) {

        // get 1 id and 2 id from currencies by code
        Optional<ReadCurrencyDto> baseCurrencyOpt = getCurrencyDtoByCode(baseCode);
        Optional<ReadCurrencyDto> targetCurrencyOpt = getCurrencyDtoByCode(targetCode);

        // Validation
        if (baseCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty()) {
            return Optional.empty();  // Одна из валют не найдена
        }

        ReadCurrencyDto readBaseCurrencyDto = baseCurrencyOpt.get();
        ReadCurrencyDto readTargetCurrencyDto = targetCurrencyOpt.get();

        // get exchangeRate
        return exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(readBaseCurrencyDto.getId(), readTargetCurrencyDto.getId())
                .map(exchangeRateEntityToReadMapper::from);
    }

    public Optional<ReadExchangeRateDto> create(CreateExchangeRateDto createExchangeRateDto) {
        // validation

        // get ReadCurrencyDto from currencies by code
        Optional<ReadCurrencyDto> baseCurrencyOpt = getCurrencyDtoByCode(createExchangeRateDto.getBaseCurrencyId());
        Optional<ReadCurrencyDto> targetCurrencyOpt = getCurrencyDtoByCode(createExchangeRateDto.getTargetCurrencyId());

        // Validation
        if (baseCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty() || !RateFormat.isValid(createExchangeRateDto.getRate())) {
            return Optional.empty();  // Одна из валют не найдена
        }

        ReadCurrencyDto readBaseCurrencyDto = baseCurrencyOpt.get();
        ReadCurrencyDto readTargetCurrencyDto = targetCurrencyOpt.get();

        // mapping
        ExchangeRatesEntity exchangeRatesEntity = createExchangeRateToEntityMapper.from(
                createExchangeRateDto,
                readBaseCurrencyDto,
                readTargetCurrencyDto
        );

        // save
        ExchangeRatesEntity savedExchangeRatesEntity = exchangeRatesDao.save(exchangeRatesEntity);

        // mapping
        ReadExchangeRateDto readExchangeRateDto = exchangeRateEntityToReadMapper.from(savedExchangeRatesEntity);

        // return
        return Optional.ofNullable(readExchangeRateDto);
    }

    public Optional<ReadExchangeRateDto> updateByCodes(String baseCode, String targetCode, String rate) {

        // get 1 id and 2 id from currencies using code
        Optional<ReadCurrencyDto> baseCurrencyOpt = getCurrencyDtoByCode(baseCode);
        Optional<ReadCurrencyDto> targetCurrencyOpt = getCurrencyDtoByCode(targetCode);

        // Validation
        if (baseCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty() || !RateFormat.isValid(rate)) {
            return Optional.empty();  // Одна из валют не найдена
        }

        ReadCurrencyDto readBaseCurrencyDto = baseCurrencyOpt.get();
        ReadCurrencyDto readTargetCurrencyDto = targetCurrencyOpt.get();

        // save
        if (exchangeRatesDao.updateByBaseCurrencyIdAndTargetCurrencyId(
                readBaseCurrencyDto.getId(),
                readTargetCurrencyDto.getId(),
                new BigDecimal(rate))) {

            return exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(readBaseCurrencyDto.getId(), readTargetCurrencyDto.getId())
                    .map(exchangeRateEntityToReadMapper::from);

        } else {
            return Optional.empty();
        }
    }

    private Optional<ReadCurrencyDto> getCurrencyDtoByCode(String code){
        return Optional.ofNullable(currencyService.findByCode(code));
    }

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }
}
