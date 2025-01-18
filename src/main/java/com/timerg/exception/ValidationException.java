package com.timerg.exception;

import com.timerg.validation.ErrorResponse;
import lombok.Getter;

import java.util.List;

public class ValidationException extends RuntimeException {
    @Getter
    private final List<ErrorResponse> errors;

    public ValidationException(List<ErrorResponse> errors) {
        super(errors != null && !errors.isEmpty()
                ? errors.get(0).getMessage()  // Устанавливаем сообщение по первой ошибке
                : "Ошибка валидации");
        this.errors = errors;
    }
}
