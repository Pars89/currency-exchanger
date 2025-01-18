package com.timerg.validation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {
    private final List<ErrorResponse> errorResponses = new ArrayList<>();

    public void add(ErrorResponse errorResponse) {
        errorResponses.add(errorResponse);
    }
    public void addAll(List<ErrorResponse> errors) {
        errorResponses.addAll(errors);
    }
    public boolean isValid() {
        return errorResponses.isEmpty();
    }

    public List<ErrorResponse> getErrorResponses() {
        return Collections.unmodifiableList(errorResponses);
    }
}
