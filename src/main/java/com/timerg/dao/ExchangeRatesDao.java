package com.timerg.dao;

import com.timerg.entity.CurrencyEntity;
import com.timerg.entity.ExchangeRatesEntity;
import com.timerg.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao implements Dao<Integer, ExchangeRatesEntity>{
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
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + "WHERE id = ?";

    private ExchangeRatesDao() {
    }
    @Override
    public ExchangeRatesEntity save(ExchangeRatesEntity exchangeRatesEntity) {
        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatement.setInt(1, exchangeRatesEntity.getBaseCurrencyId());
            prepareStatement.setInt(2, exchangeRatesEntity.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3, exchangeRatesEntity.getRate());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                exchangeRatesEntity.setId(generatedKeys.getInt(1));
            }

            return exchangeRatesEntity;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ExchangeRatesEntity exchangeRatesEntity) {
        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {

            prepareStatement.setInt(1, exchangeRatesEntity.getBaseCurrencyId());
            prepareStatement.setInt(2, exchangeRatesEntity.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3, exchangeRatesEntity.getRate());

            prepareStatement.setInt(4, exchangeRatesEntity.getId());

            prepareStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRatesEntity> findById(Integer id) {
        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            prepareStatement.setInt(1, id);

            var resultSet = prepareStatement.executeQuery();

            ExchangeRatesEntity exchangeRatesEntity = null;

            if (resultSet.next()) {
                exchangeRatesEntity = buildExchangeRatesEntity(resultSet);
            }

            return Optional.ofNullable(exchangeRatesEntity);

        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    private ExchangeRatesEntity buildExchangeRatesEntity(ResultSet resultSet) throws SQLException {
        return new ExchangeRatesEntity()
                .builder()
                .id(resultSet.getInt("id"))
                .BaseCurrencyId(resultSet.getInt("BaseCurrencyId"))
                .TargetCurrencyId(resultSet.getInt("TargetCurrencyId"))
                .Rate(resultSet.getBigDecimal("Rate"))
                .build();
    }

    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }
}
