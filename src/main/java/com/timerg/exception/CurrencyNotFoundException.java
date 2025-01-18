package com.timerg.exception;

public class CurrencyNotFoundException extends RuntimeException{
    public CurrencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyNotFoundException(String message) {
        super(message);
    }

    public CurrencyNotFoundException(Throwable cause) {
        super(cause);
    }
}
