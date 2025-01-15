package com.timerg.dto;


import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.Objects;


@Builder
public class CreateCurrencyDto {
    private final String code;
    private final String fullName;
    private final String sign;

    public CreateCurrencyDto(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateCurrencyDto that = (CreateCurrencyDto) o;
        return Objects.equals(code, that.code) && Objects.equals(fullName, that.fullName) && Objects.equals(sign, that.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, fullName, sign);
    }

    @Override
    public String toString() {
        return "CreateCurrencyDto{" +
               "code='" + code + '\'' +
               ", fullName='" + fullName + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }
}
