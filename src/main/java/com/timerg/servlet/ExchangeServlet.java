package com.timerg.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timerg.dto.ConvertedExchangeRateDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.exception.CurrencyNotFoundException;
import com.timerg.exception.ValidationException;
import com.timerg.service.CurrencyService;
import com.timerg.service.ExchangeRatesService;
import com.timerg.validation.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));

            ReadCurrencyDto fromCurrency = currencyService.findByCode(from);
            ReadCurrencyDto toCurrency = currencyService.findByCode(to);

            BigDecimal rate = calculateRate(from, to);
            BigDecimal convertedAmount = rate.multiply(amount);

            ConvertedExchangeRateDto responseDto = createConvertedExchangeRateDto(
                    fromCurrency, toCurrency, rate, amount, convertedAmount
            );

            sendJsonResponse(resp, HttpServletResponse.SC_OK, responseDto);
        } catch (ValidationException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }  catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
        }
    }

    private BigDecimal calculateRate(String from, String to) {
        try {
            return getDirectRate(from, to);
        } catch (CurrencyNotFoundException e) {
            // Продолжаем, если прямой курс не найден
        }

        try {
            return getReverseRate(from, to);
        } catch (CurrencyNotFoundException e) {
            // Продолжаем, если обратный курс не найден
        }

        try {
            return getRateViaUsd(from, to);
        } catch (CurrencyNotFoundException e) {
            throw new RuntimeException("Невозможно рассчитать курс для выбранных валют.");
        }
    }
    private BigDecimal getDirectRate(String from, String to) throws CurrencyNotFoundException {
        ReadExchangeRateDto directRateDto = exchangeRatesService.findByCodes(from, to);
        return directRateDto.getRate();
    }

    private BigDecimal getReverseRate(String from, String to) throws CurrencyNotFoundException {
        ReadExchangeRateDto reverseRateDto = exchangeRatesService.findByCodes(to, from);
        return BigDecimal.ONE.divide(reverseRateDto.getRate(), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal getRateViaUsd(String from, String to) throws CurrencyNotFoundException {
        String codeUSD = "USD";
        ReadExchangeRateDto usdToFromRateDto = exchangeRatesService.findByCodes(codeUSD, from);
        ReadExchangeRateDto usdToToRateDto = exchangeRatesService.findByCodes(codeUSD, to);

        BigDecimal usdToFromRate = usdToFromRateDto.getRate();
        BigDecimal usdToToRate = usdToToRateDto.getRate();

        return usdToFromRate.divide(usdToToRate, 6, RoundingMode.HALF_UP);
    }

    private ConvertedExchangeRateDto createConvertedExchangeRateDto(ReadCurrencyDto from,
                                                                    ReadCurrencyDto to,
                                                                    BigDecimal rate,
                                                                    BigDecimal amount,
                                                                    BigDecimal convertedAmount) {
        return ConvertedExchangeRateDto.builder()
                .baseCurrencyId(from)
                .targetCurrencyId(to)
                .rate(rate)
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();
    }
    private void sendJsonResponse(HttpServletResponse resp, int status,  Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(objectMapper.writeValueAsString(data));
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(objectMapper.writeValueAsString(ErrorResponse.of(String.valueOf(status), message)));
        }
    }
}
