package com.timerg.service;

import com.timerg.dao.ExchangeRatesDao;
import com.timerg.dto.CreateExchangeRateDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.entity.ExchangeRatesEntity;
import com.timerg.mapper.CurrencyEntityMapper;
import com.timerg.mapper.ReadCurrencyMapper;
import com.timerg.util.RateFormat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExchangeRatesService {
    private static final ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final ReadCurrencyMapper readCurrencyMapper = ReadCurrencyMapper.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final CurrencyEntityMapper currencyEntityMapper = CurrencyEntityMapper.getInstance();

    private ExchangeRatesService(){
    }

    public List<ReadExchangeRateDto> findAll() {
        return exchangeRatesDao.findAll().stream()
                .map(exchangeRatesEntity -> ReadExchangeRateDto.builder()
                        .id(exchangeRatesEntity.getId())
                        .baseCurrencyId(readCurrencyMapper.from(exchangeRatesEntity.getBaseCurrencyId()))
                        .targetCurrencyId(readCurrencyMapper.from(exchangeRatesEntity.getTargetCurrencyId()))
                        .rate(exchangeRatesEntity.getRate())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public Optional<ReadExchangeRateDto> findByCodes(String pairCode) {
        // parse
        String baseCode = pairCode.substring(0, 3).toUpperCase();
        String targetCode = pairCode.substring(3, 6).toUpperCase();

        // get 1 id and 2 id from currencies using code
        Optional<ReadCurrencyDto> baseCurrencyOpt = currencyService.findByCode(baseCode);
        Optional<ReadCurrencyDto> targetCurrencyOpt = currencyService.findByCode(targetCode);

        // Validation
        if (baseCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty()) {
            return Optional.empty();  // Одна из валют не найдена
        }

        ReadCurrencyDto readBaseCurrencyDto = baseCurrencyOpt.get();
        ReadCurrencyDto readTargetCurrencyDto = targetCurrencyOpt.get();

        // get findByBaseCurrencyIdAndTargetCurrencyId
        return exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(readBaseCurrencyDto.getId(), readTargetCurrencyDto.getId())
                .map(exchangeRatesEntity -> ReadExchangeRateDto.builder()
                        .id(exchangeRatesEntity.getId())
                        .baseCurrencyId(readBaseCurrencyDto)
                        .targetCurrencyId(readTargetCurrencyDto)
                        .rate(exchangeRatesEntity.getRate())
                        .build());
    }

    public Optional<ReadExchangeRateDto> create(CreateExchangeRateDto createExchangeRateDto) {
        // validation
        // get ReadCurrencyDto from currencies using code
        Optional<ReadCurrencyDto> baseCurrencyOpt = currencyService.findByCode(createExchangeRateDto.getBaseCurrencyId());
        Optional<ReadCurrencyDto> targetCurrencyOpt = currencyService.findByCode(createExchangeRateDto.getTargetCurrencyId());

        // Validation
        if (baseCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty() || !RateFormat.isValid(createExchangeRateDto.getRate())) {
            return Optional.empty();  // Одна из валют не найдена
        }

        ReadCurrencyDto readBaseCurrencyDto = baseCurrencyOpt.get();
        ReadCurrencyDto readTargetCurrencyDto = targetCurrencyOpt.get();

        // mapping
        ExchangeRatesEntity exchangeRatesEntity = ExchangeRatesEntity.builder()
                .BaseCurrencyId(currencyEntityMapper.from(readBaseCurrencyDto))
                .TargetCurrencyId(currencyEntityMapper.from(readTargetCurrencyDto))
                .rate(new BigDecimal(createExchangeRateDto.getRate()))
                .build();


        // save
        ExchangeRatesEntity savedExchangeRatesEntity = exchangeRatesDao.save(exchangeRatesEntity);


        // mapping and return
        return Optional.of(ReadExchangeRateDto.builder()
                .id(savedExchangeRatesEntity.getId())
                .baseCurrencyId(readBaseCurrencyDto)
                .targetCurrencyId(readTargetCurrencyDto)
                .rate(savedExchangeRatesEntity.getRate())
                .build());
    }

    public Optional<ReadExchangeRateDto> updateByCodes(String pairCode, String rate) {
        // parse
        String baseCode = pairCode.substring(0, 3).toUpperCase();
        String targetCode = pairCode.substring(3, 6).toUpperCase();

        // get 1 id and 2 id from currencies using code
        Optional<ReadCurrencyDto> baseCurrencyOpt = currencyService.findByCode(baseCode);
        Optional<ReadCurrencyDto> targetCurrencyOpt = currencyService.findByCode(targetCode);

        // Validation
        if (baseCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty() || !RateFormat.isValid(rate)) {
            return Optional.empty();  // Одна из валют не найдена
        }

        ReadCurrencyDto readBaseCurrencyDto = baseCurrencyOpt.get();
        ReadCurrencyDto readTargetCurrencyDto = targetCurrencyOpt.get();

        // save updateByBaseCurrencyIdAndTargetCurrencyId
        return exchangeRatesDao.updateByBaseCurrencyIdAndTargetCurrencyId(readBaseCurrencyDto.getId(), readTargetCurrencyDto.getId(), new BigDecimal(rate))
                .map(exchangeRatesEntity -> ReadExchangeRateDto.builder()
                        .id(exchangeRatesEntity.getId())
                        .baseCurrencyId(readBaseCurrencyDto)
                        .targetCurrencyId(readTargetCurrencyDto)
                        .rate(exchangeRatesEntity.getRate())
                        .build());
    }

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }


}
