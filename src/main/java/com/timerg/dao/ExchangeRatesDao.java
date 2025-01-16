package com.timerg.dao;

import com.timerg.entity.CurrencyEntity;
import com.timerg.entity.ExchangeRatesEntity;
import com.timerg.exception.DaoException;
import com.timerg.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao implements Dao<Integer, ExchangeRatesEntity> {
    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();
    private static final String SAVE_SQL = """
            INSERT INTO ExchangeRates
            (BaseCurrencyId,
            TargetCurrencyId,
            Rate)
            VALUES (?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE ExchangeRates
            SET 
            BaseCurrencyId = ?,
            TargetCurrencyId = ?,
            Rate = ?
            WHERE id = ?               
            """;
    private static final String DELETE_SQL = """
            DELETE FROM ExchangeRates
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT
            id,
            BaseCurrencyId,
            TargetCurrencyId,
            Rate
            FROM ExchangeRates
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE id = ?";
    private static final String FIND_BY_BASE_CURRENCY_ID_AND_TARGET_CURRENCY_ID_SQL = FIND_ALL_SQL + """
            WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
            """;
    private static final String UPDATE_BY_BASE_CURRENCY_ID_AND_TARGET_CURRENCY_ID_SQL = """
            UPDATE ExchangeRates
            SET 
            Rate = ?
            WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?           
            """;

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRatesDao() {
    }

    @Override
    public ExchangeRatesEntity save(ExchangeRatesEntity exchangeRatesEntity) {
        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatement.setInt(1, exchangeRatesEntity.getBaseCurrencyId().getId());
            prepareStatement.setInt(2, exchangeRatesEntity.getTargetCurrencyId().getId());
            prepareStatement.setBigDecimal(3, exchangeRatesEntity.getRate());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                exchangeRatesEntity.setId(generatedKeys.getInt(1));
            }

            return exchangeRatesEntity;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(ExchangeRatesEntity exchangeRatesEntity) {
        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {

            prepareStatement.setInt(1, exchangeRatesEntity.getBaseCurrencyId().getId());
            prepareStatement.setInt(2, exchangeRatesEntity.getTargetCurrencyId().getId());
            prepareStatement.setBigDecimal(3, exchangeRatesEntity.getRate());

            prepareStatement.setInt(4, exchangeRatesEntity.getId());

            prepareStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {

            prepareStatement.setInt(1, id);

            var executeUpdate = prepareStatement.executeUpdate();

            return executeUpdate > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
    @Override
    public List<ExchangeRatesEntity> findAll() {

        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();

            ExchangeRatesEntity exchangeRatesEntity = null;

            List<ExchangeRatesEntity> exchangeRatesEntities = new ArrayList<>();

            while (resultSet.next()) {
                exchangeRatesEntity = buildExchangeRatesEntity(resultSet);
                exchangeRatesEntities.add(exchangeRatesEntity);
            }

            return exchangeRatesEntities;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<ExchangeRatesEntity> findById(Integer id) {
        try (Connection connection = ConnectionManager.get()) {
            return findBy(connection, FIND_BY_ID_SQL, id);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<ExchangeRatesEntity> findByBaseCurrencyIdAndTargetCurrencyId(Integer baseCurrencyId, Integer targetCurrencyId) {
        try (Connection connection = ConnectionManager.get()) {
            return findBy(connection, FIND_BY_BASE_CURRENCY_ID_AND_TARGET_CURRENCY_ID_SQL, baseCurrencyId, targetCurrencyId);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Optional<ExchangeRatesEntity> findBy(Connection connection, String sqlQuery, Object... params) {
        try (var prepareStatement = connection.prepareStatement(sqlQuery)) {

            for (int i = 0; i < params.length; i++) {
                prepareStatement.setObject(i + 1, params[i]);
            }

            var resultSet = prepareStatement.executeQuery();

            ExchangeRatesEntity exchangeRatesEntity = null;

            if (resultSet.next()) {
                exchangeRatesEntity = buildExchangeRatesEntity(resultSet);
            }

            return Optional.ofNullable(exchangeRatesEntity);

        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    public boolean updateByBaseCurrencyIdAndTargetCurrencyId(Integer baseCurrencyId, Integer targetCurrencyId, BigDecimal rate) {
        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_BY_BASE_CURRENCY_ID_AND_TARGET_CURRENCY_ID_SQL)) {

            prepareStatement.setBigDecimal(1, rate);
            prepareStatement.setInt(2, baseCurrencyId);
            prepareStatement.setInt(3, targetCurrencyId);

            return prepareStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private ExchangeRatesEntity buildExchangeRatesEntity(ResultSet resultSet) throws SQLException {

        Optional<CurrencyEntity> baseCurrencyId = currencyDao.findById(resultSet.getStatement().getConnection(), resultSet.getInt("BaseCurrencyId"));
        Optional<CurrencyEntity> targetCurrencyId = currencyDao.findById(resultSet.getStatement().getConnection(), resultSet.getInt("TargetCurrencyId"));

        return ExchangeRatesEntity.builder()
                .id(resultSet.getInt("id"))
                .baseCurrencyId(baseCurrencyId.orElse(null))
                .targetCurrencyId(targetCurrencyId.orElse(null))
                .rate(resultSet.getBigDecimal("Rate"))
                .build();
    }

    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }
}
