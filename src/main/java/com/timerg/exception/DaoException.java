package com.timerg.exception;

import java.sql.SQLException;

public class DaoException extends RuntimeException {

    private static final String MESSAGE_ERROR = "База данных недоступна";
    public DaoException(SQLException e) {
        super(MESSAGE_ERROR);
    }
}
