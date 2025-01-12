package com.timerg.dao;

import com.timerg.entity.CurrencyEntity;
import com.timerg.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Integer, CurrencyEntity> {

    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private static final String SAVE_SQL = """
            INSERT INTO Currencies
            (Code,
            FullName,
            Sign)
            VALUES (?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE Currencies
            SET 
            Code = ?,
            FullName = ?,
            Sign = ?
            WHERE id = ?               
            """;
    private static final String DELETE_SQL = """
            DELETE FROM Currencies
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT
            id,
            Code,
            FullName,
            Sign
            FROM Currencies
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + "WHERE id = ?";

    private CurrencyDao() {
    }


    @Override
    public CurrencyEntity save(CurrencyEntity currencyEntity) {
        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatement.setString(1, currencyEntity.getCode());
            prepareStatement.setString(2, currencyEntity.getFullName());
            prepareStatement.setString(3, currencyEntity.getSign());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                currencyEntity.setId(generatedKeys.getInt(1));
            }

            return currencyEntity;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(CurrencyEntity currencyEntity) {
        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {

            prepareStatement.setString(1, currencyEntity.getCode());
            prepareStatement.setString(2, currencyEntity.getFullName());
            prepareStatement.setString(3, currencyEntity.getSign());

            prepareStatement.setInt(4, currencyEntity.getId());

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
    public Optional<CurrencyEntity> findById(Integer id) {

        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            prepareStatement.setInt(1, id);

            var resultSet = prepareStatement.executeQuery();

            CurrencyEntity entity = null;

            if (resultSet.next()) {
                entity = buildCurrencyEntity(resultSet);
            }

            return Optional.ofNullable(entity);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CurrencyEntity> findAll() {

        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();

            CurrencyEntity currencyEntity = null;

            List<CurrencyEntity> currencies = new ArrayList<>();

            while (resultSet.next()) {
                currencyEntity = buildCurrencyEntity(resultSet);
                currencies.add(currencyEntity);
            }

            return currencies;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private CurrencyEntity buildCurrencyEntity(ResultSet resultSet) throws SQLException {
        return new CurrencyEntity()
                .builder()
                .id(resultSet.getInt("id"))
                .code(resultSet.getString("Code"))
                .fullName(resultSet.getString("FullName"))
                .sign(resultSet.getString("Sign"))
                .build();
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }
}
