package com.timerg.validation;

import com.timerg.dto.CreateCurrencyDto;

public class CreateCurrencyValidator implements Validator<CreateCurrencyDto>{
    private static final CreateCurrencyValidator INSTANCE = new CreateCurrencyValidator();
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_SIGN_LENGTH = 5;
    private static final String ERROR_MISSING_NAME = "Отсутствует поле формы name";
    private static final String ERROR_MISSING_CODE = "Отсутствует поле формы code";
    private static final String ERROR_MISSING_SIGN = "Отсутствует поле формы sign";
    private static final String ERROR_NAME_TOO_LONG = "Поле name не должно превышать 50 символов";
    private static final String ERROR_SIGN_TOO_LONG = "Поле sign не должно превышать 5 символов";
    private final CodeCurrencyValidator codeCurrencyValidator = CodeCurrencyValidator.getInstance();

    private CreateCurrencyValidator(){
    }

    @Override
    public ValidationResult isValid(CreateCurrencyDto createCurrencyDto) {

        ValidationResult result = new ValidationResult();

        validateRequiredField(createCurrencyDto.getFullName(), ERROR_MISSING_NAME, result);
        validateRequiredField(createCurrencyDto.getCode(), ERROR_MISSING_CODE, result);
        validateRequiredField(createCurrencyDto.getSign(), ERROR_MISSING_SIGN, result);


        ValidationResult codeValidation = codeCurrencyValidator.isValid(createCurrencyDto.getCode());
        result.addAll(codeValidation.getErrorResponses());

        validateLength(createCurrencyDto.getFullName(), MAX_NAME_LENGTH, ERROR_NAME_TOO_LONG, result);
        validateLength(createCurrencyDto.getSign(), MAX_SIGN_LENGTH, ERROR_SIGN_TOO_LONG, result);

        return result;
    }

    private void validateRequiredField(String field, String errorMessage, ValidationResult result) {
        if (field == null || field.trim().isEmpty()) {
            result.add(ErrorResponse.of(ErrorCode.BAD_REQUEST.getCode(), errorMessage));
        }
    }

    private void validateLength(String field, int maxLength, String errorMessage, ValidationResult result) {
        if (field != null && field.length() > maxLength) {
            result.add(ErrorResponse.of(ErrorCode.BAD_REQUEST.getCode(), errorMessage));
        }
    }

    public static CreateCurrencyValidator getInstance() {
        return INSTANCE;
    }
}
