package com.timerg.validation;

public enum ErrorCode {
    BAD_REQUEST("400"),
    NOT_FOUND("404"),
    CONFLICT("409"),
    INTERNAL_SERVER_ERROR("500");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
