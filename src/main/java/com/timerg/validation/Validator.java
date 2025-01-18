package com.timerg.validation;

public interface Validator<T> {
    ValidationResult isValid(T object);
}
