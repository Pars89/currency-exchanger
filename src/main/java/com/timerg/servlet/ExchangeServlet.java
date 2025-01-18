package com.timerg.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timerg.dto.ConvertedExchangeRateDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.service.CurrencyService;
import com.timerg.service.ExchangeRatesService;
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
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));

        BigDecimal convertedAmount;
        BigDecimal rate;
        ConvertedExchangeRateDto responseDto = null;

        Optional<ReadCurrencyDto> fromCurrency = Optional.ofNullable(currencyService.findByCode(from));
        Optional<ReadCurrencyDto> toCurrency = Optional.ofNullable(currencyService.findByCode(to));

        // calculate AB rate
        Optional<ReadExchangeRateDto> directRateDto = exchangeRatesService.findByCodes(from, to);
        if (directRateDto.isPresent()) {
            rate = directRateDto.get().getRate();
            convertedAmount = rate.multiply(amount);

            responseDto = createConvertedExchangeRateDto(fromCurrency.get(), toCurrency.get(), rate, amount, convertedAmount);

        } else {
            // calculate BA rate
            Optional<ReadExchangeRateDto> reverseRateDto = exchangeRatesService.findByCodes(to, from);

            if (reverseRateDto.isPresent()) {
                rate = BigDecimal.ONE.divide(reverseRateDto.get().getRate(), 6, RoundingMode.HALF_UP);
                convertedAmount = rate.multiply(amount);

                responseDto = createConvertedExchangeRateDto(fromCurrency.get(), toCurrency.get(), rate, amount, convertedAmount);

            } else {
                // calculate ( (USD-A) / (USD-B) )
                String codeUSD = "USD";
                Optional<ReadExchangeRateDto> usdToFromRateDto = exchangeRatesService.findByCodes(codeUSD, from);
                Optional<ReadExchangeRateDto> usdToToRateDto = exchangeRatesService.findByCodes(codeUSD, to);

                if (usdToFromRateDto.isPresent() && usdToToRateDto.isPresent()) {

                    BigDecimal usdToFromRate = usdToFromRateDto.get().getRate();
                    BigDecimal usdToToRate = usdToToRateDto.get().getRate();

                    rate = usdToFromRate.divide(usdToToRate, 6, RoundingMode.HALF_UP);
                    convertedAmount = rate.multiply(amount);

                    responseDto = createConvertedExchangeRateDto(fromCurrency.get(), toCurrency.get(), rate, amount, convertedAmount);

                } else {
                    // Если курс не найден, ошибка
                    throw new RuntimeException("Невозможно рассчитать курс для выбранных валют.");
                }
            }
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(objectMapper.writeValueAsString(responseDto));
        }
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
}
