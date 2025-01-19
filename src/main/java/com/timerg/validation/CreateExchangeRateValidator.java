package com.timerg.validation;

import com.timerg.dto.CreateExchangeRateDto;

import java.math.BigDecimal;

public class CreateExchangeRateValidator implements Validator<CreateExchangeRateDto> {

    private static final CreateExchangeRateValidator INSTANCE = new CreateExchangeRateValidator();
    private static final String ERROR_MISSING_BASE_CURRENCY = "Отсутствует поле baseCurrencyId";
    private static final String ERROR_MISSING_TARGET_CURRENCY = "Отсутствует поле targetCurrencyId";
    private static final String ERROR_MISSING_RATE = "Отсутствует поле rate";
    private static final String ERROR_INVALID_RATE_FORMAT = "Поле rate должно быть числом";
    private static final String ERROR_NEGATIVE_RATE = "Поле rate должно быть положительным числом";

    private CreateExchangeRateValidator() {
    }

    @Override
    public ValidationResult isValid(CreateExchangeRateDto createExchangeRateDto) {
        ValidationResult validationResult = new ValidationResult();

        validateRequiredField(createExchangeRateDto.getBaseCurrencyId(), ERROR_MISSING_BASE_CURRENCY, validationResult);
        validateRequiredField(createExchangeRateDto.getTargetCurrencyId(), ERROR_MISSING_TARGET_CURRENCY, validationResult);
        validateRate(createExchangeRateDto.getRate(), validationResult);

        // Валидация значения rate
        validateRate(createExchangeRateDto.getRate(), validationResult);


        return validationResult;
    }

    private void validateRequiredField(String field, String errorMessage, ValidationResult result) {
        if (field == null || field.trim().isEmpty()) {
            result.add(ErrorResponse.of(ErrorCode.BAD_REQUEST.getCode(), errorMessage));
        }
    }

    private void validateRate(String rate, ValidationResult result) {
        if (rate == null || rate.trim().isEmpty()) {
            result.add(ErrorResponse.of(ErrorCode.BAD_REQUEST.getCode(), ERROR_MISSING_RATE));
            return;
        }

        try {
            BigDecimal rateValue = new BigDecimal(rate);
            if (rateValue.compareTo(BigDecimal.ZERO) <= 0) {
                result.add(ErrorResponse.of(ErrorCode.BAD_REQUEST.getCode(), ERROR_NEGATIVE_RATE));
            }
        } catch (NumberFormatException e) {
            result.add(ErrorResponse.of(ErrorCode.BAD_REQUEST.getCode(), ERROR_INVALID_RATE_FORMAT));
        }
    }

    public static CreateExchangeRateValidator getInstance() {
        return INSTANCE;
    }
}
