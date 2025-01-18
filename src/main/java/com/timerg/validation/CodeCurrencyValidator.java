package com.timerg.validation;

public class CodeCurrencyValidator implements Validator<String> {
    private static final CodeCurrencyValidator INSTANCE = new CodeCurrencyValidator();
    private static final String CODE_PATTERN = "^[A-Z]{3}$";
    private static final String ERROR_INVALID_CODE_FORMAT = "Поле code должно состоять из 3 заглавных латинских букв";

    private CodeCurrencyValidator() {
    }

    @Override
    public ValidationResult isValid(String code) {
        ValidationResult result = new ValidationResult();

        validateCodeFormat(code, ERROR_INVALID_CODE_FORMAT, result);

        return result;
    }


    private void validateCodeFormat(String code, String errorMessage, ValidationResult result) {
        if (code != null && !code.matches(CODE_PATTERN)) {
            result.add(ErrorResponse.of(ErrorCode.BAD_REQUEST.getCode(), errorMessage));
        }
    }

    public static CodeCurrencyValidator getInstance() {
        return INSTANCE;
    }
}
