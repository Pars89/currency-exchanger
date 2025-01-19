package com.timerg.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timerg.dto.CreateExchangeRateDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.exception.CurrencyNotFoundException;
import com.timerg.exception.ExchangeRateAlreadyExistException;
import com.timerg.exception.ValidationException;
import com.timerg.service.ExchangeRatesService;
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
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ReadExchangeRateDto> exchangeRatesServiceAll = exchangeRatesService.findAll();
            sendJsonResponse(resp, HttpServletResponse.SC_OK, exchangeRatesServiceAll);
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
        }
        
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            ReadExchangeRateDto readExchangeRateDto = extractDtoFromRequest(req);
            sendJsonResponse(resp, HttpServletResponse.SC_CREATED, readExchangeRateDto);
        } catch (ValidationException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ExchangeRateAlreadyExistException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
        }

    }

    private ReadExchangeRateDto extractDtoFromRequest(HttpServletRequest req) {
        return exchangeRatesService.create(
                CreateExchangeRateDto.builder()
                        .baseCurrencyId(req.getParameter("baseCurrencyCode"))
                        .targetCurrencyId(req.getParameter("targetCurrencyCode"))
                        .rate(req.getParameter("rate"))
                        .build());
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
}
