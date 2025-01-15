package com.timerg;

import com.timerg.dao.CurrencyDao;
import com.timerg.dao.ExchangeRatesDao;
import com.timerg.entity.CurrencyEntity;
import com.timerg.entity.ExchangeRatesEntity;
import com.timerg.service.CurrencyService;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] argsStrings) {

        // TEST
        CurrencyDao currencyDao = CurrencyDao.getInstance();

        CurrencyService currencyService= CurrencyService.getInstance();

        CurrencyEntity currencyEntity = new CurrencyEntity()
                .builder()
                .code("EUR")
                .fullName("Euro")
                .sign("E")
                .build();

//        currencyDao.save(currencyEntity);
//        currencyDao.update(currencyEntity);
//        Optional<CurrencyEntity> entity = currencyDao.findById(1);
//        List<CurrencyEntity> all = currencyDao.findAll();
//        boolean delete = currencyDao.delete(5);
//        System.out.println(delete);

        ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();

        ExchangeRatesEntity exchangeRatesEntity = new ExchangeRatesEntity()
                .builder()
                .BaseCurrencyId(currencyDao.findById(7).orElse(null))
                .TargetCurrencyId(currencyDao.findById(8).orElse(null))
                .rate(BigDecimal.valueOf(1.6))
                .build();

//        exchangeRatesDao.save(exchangeRatesEntity);
//        exchangeRatesDao.update(exchangeRatesEntity);
//        Optional<ExchangeRatesEntity> exchangeRatesDaoById = exchangeRatesDao.findById(1);
//        List<ExchangeRatesEntity> all = exchangeRatesDao.findAll();
//        boolean delete = exchangeRatesDao.delete(2);
//        System.out.println(delete);


//        List<CurrencyDto> all = currencyService.findAll();
//        System.out.println(all);
    }
}
