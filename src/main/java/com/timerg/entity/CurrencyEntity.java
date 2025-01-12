package com.timerg.entity;

import lombok.Builder;

import java.util.Objects;

@Builder
public class CurrencyEntity {
    private Integer id;
    private String code;
    private String fullName;
    private String sign;

    public CurrencyEntity() {
    }

    public CurrencyEntity(Integer id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyEntity that = (CurrencyEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(fullName, that.fullName) && Objects.equals(sign, that.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullName, sign);
    }

    @Override
    public String toString() {
        return "CurrencyEntity{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", fullName='" + fullName + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }
}
