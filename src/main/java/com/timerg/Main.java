package com.timerg;

import com.timerg.dao.CurrencyDao;
import com.timerg.entity.CurrencyEntity;
import com.timerg.util.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] argsStrings) {

        CurrencyDao currencyDao = CurrencyDao.getInstance();

        CurrencyEntity currencyEntity = new CurrencyEntity()
                .builder()
                .id(4)
                .code("AFN")
                .fullName("Afghani")
                .sign("3d")
                .build();


//        currencyDao.save(currencyEntity);
//        currencyDao.update(currencyEntity);
//        Optional<CurrencyEntity> entity = currencyDao.findById(4);
//        List<CurrencyEntity> all = currencyDao.findAll();
//        boolean delete = currencyDao.delete(4);
//        System.out.println(delete);
    }
}
