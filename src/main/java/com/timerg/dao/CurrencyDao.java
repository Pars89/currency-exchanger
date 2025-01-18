package com.timerg.dao;

import com.timerg.entity.CurrencyEntity;
import com.timerg.exception.DaoException;
import com.timerg.util.ConnectionManager;
import lombok.SneakyThrows;

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
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE id = ?";
    private static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + " WHERE Code = ?";

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
            throw new DaoException("Error saving CurrencyEntity to the database", e);
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
            throw new DaoException("Error updating CurrencyEntity in the database", e);
        }
    }

    @Override
    public boolean delete(Integer id) {

        try (Connection connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {

            prepareStatement.setInt(1, id);

            var executeUpdate = prepareStatement.executeUpdate();

            return executeUpdate == 1;

        } catch (SQLException e) {
            throw new DaoException("Error deleting CurrencyEntity in the database", e);
        }
    }

    @Override
    public Optional<CurrencyEntity> findById(Integer id) {
        try (Connection connection = ConnectionManager.get()) {
            return findById(connection, id);
        } catch (SQLException e) {
            throw new DaoException("Error finding CurrencyEntity by id in the database", e);
        }
    }

    public Optional<CurrencyEntity> findById(Connection connection, int currencyId) {
        return findBy(connection, FIND_BY_ID_SQL, currencyId);
    }

    public Optional<CurrencyEntity> findByCode(String code) {
        try (Connection connection = ConnectionManager.get()) {
            return findBy(connection, FIND_BY_CODE_SQL, code);
        } catch (SQLException e) {
            throw new DaoException("Error finding CurrencyEntity by code in the database", e);
        }
    }

    public boolean existByCode(String code) {
        try (Connection connection = ConnectionManager.get()) {
            return findBy(connection, FIND_BY_CODE_SQL, code).isPresent();
            // Changed FIND_BY_CODE_SQL On EXIST_BY_CODE_SQL (for performance)
        } catch (SQLException e) {
            throw new DaoException("Error existing CurrencyEntity by code in the database", e);
        }
    }

    private Optional<CurrencyEntity> findBy(Connection connection, String sqlQuery, Object param) {
        try (var prepareStatement = connection.prepareStatement(sqlQuery)) {

            prepareStatement.setObject(1, param);

            var resultSet = prepareStatement.executeQuery();

            CurrencyEntity entity = null;

            if (resultSet.next()) {
                entity = buildCurrencyEntity(resultSet);
            }

            return Optional.ofNullable(entity);

        } catch (SQLException e) {
            throw new DaoException("Error finding CurrencyEntity by ... in the database", e);
        }
    }

    @Override
    public List<CurrencyEntity> findAll() {

        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();

            List<CurrencyEntity> currencies = new ArrayList<>();

            while (resultSet.next()) {
                currencies.add(buildCurrencyEntity(resultSet));
            }

            return currencies;

        } catch (SQLException e) {
            throw new DaoException("Error finding all CurrencyEntity in the database", e);
        }
    }

    private CurrencyEntity buildCurrencyEntity(ResultSet resultSet) throws SQLException {
        return CurrencyEntity.builder()
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
