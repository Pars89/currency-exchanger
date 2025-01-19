package com.timerg.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.timerg.dto.CreateExchangeRateDto;
import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.exception.CurrencyNotFoundException;
import com.timerg.exception.ValidationException;
import com.timerg.service.ExchangeRatesService;
import com.timerg.validation.ErrorCode;
import com.timerg.validation.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals("PATCH")) {
            super.service(req, resp);
        }

        this.doPatch(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            CurrencyPairDto currencyPairDto = parseCurrencyPair(req);
            ReadExchangeRateDto readExchangeRateDto = exchangeRatesService.findByCodes(currencyPairDto.baseCode, currencyPairDto.targetCode());
            sendJsonResponse(resp, HttpServletResponse.SC_OK, readExchangeRateDto);

        } catch (ValidationException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            CreateExchangeRateDto createExchangeRateDto = extractDtoFromRequest(req);
            ReadExchangeRateDto readExchangeRateDto = exchangeRatesService.updateByCodes(createExchangeRateDto);

            sendJsonResponse(resp, HttpServletResponse.SC_OK, readExchangeRateDto);
        } catch (ValidationException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
        }
    }

    private CreateExchangeRateDto extractDtoFromRequest(HttpServletRequest req) throws IOException{

        // Обернуть в блок try
        String[] strings = req.getRequestURI().split("/");
        String baseCode = strings[2].substring(0, 3).toUpperCase();
        String targetCode = strings[2].substring(3, 6).toUpperCase();

        String rate = req.getReader().readLine().split("=")[1];

        return CreateExchangeRateDto.builder()
                .baseCurrencyId(baseCode)
                .targetCurrencyId(targetCode)
                .rate(rate)
                .build();
    }

    private CurrencyPairDto parseCurrencyPair(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();

        if (pathInfo  == null || pathInfo.length() != 7) {
            throw new ValidationException(List.of(ErrorResponse.of("400", "Некорректный формат валютной пары в URI")));
        }

        String baseCode = pathInfo.substring(1, 4).toUpperCase();
        String targetCode = pathInfo.substring(4, 7).toUpperCase();

        return new CurrencyPairDto(baseCode, targetCode);
    }

    private void sendJsonResponse(HttpServletResponse resp, int status,  Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(objectMapper.writeValueAsString(data));
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(objectMapper.writeValueAsString(ErrorResponse.of(String.valueOf(status), message)));
        }
    }

    private record CurrencyPairDto(String baseCode, String targetCode) {}
}
