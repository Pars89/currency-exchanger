package com.timerg.exception;

public class ExchangeRateAlreadyExistException extends RuntimeException{
    public ExchangeRateAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeRateAlreadyExistException(String message) {
        super(message);
    }

    public ExchangeRateAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
