package com.timerg.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public class RateFormat {

    public BigDecimal format(String rate) {
        return new BigDecimal(rate);
    }


    public boolean isValid(String rate) {
        try {
            // ? не доконца понимаю
            return Optional.ofNullable(rate)
                    .map(RateFormat::format)
                    .isPresent();
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
