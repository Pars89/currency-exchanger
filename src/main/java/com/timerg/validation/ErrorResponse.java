package com.timerg.validation;

import lombok.Value;

@Value(staticConstructor = "of")
public class ErrorResponse {
    String code;
    String message;
}
