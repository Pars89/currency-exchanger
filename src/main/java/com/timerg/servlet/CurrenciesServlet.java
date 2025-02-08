package com.timerg.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timerg.dto.CreateCurrencyDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.exception.CurrencyAlreadyExistsException;
import com.timerg.exception.DaoException;
import com.timerg.exception.ValidationException;
import com.timerg.mapper.CreateCurrencyToEntityMapper;
import com.timerg.service.CurrencyService;
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

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            List<ReadCurrencyDto> currencies = currencyService.findAll();
            sendJsonResponse(resp, currencies, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            CreateCurrencyDto createCurrencyDto = extractDtoFromRequest(req);

            ReadCurrencyDto readCurrencyDto = currencyService.create(createCurrencyDto);

            sendJsonResponse(resp, readCurrencyDto, HttpServletResponse.SC_CREATED);

        } catch (ValidationException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyAlreadyExistsException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
        }
    }
    private void sendJsonResponse(HttpServletResponse resp, Object data, int status) throws IOException {
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

    private CreateCurrencyDto extractDtoFromRequest(HttpServletRequest req) {
        return CreateCurrencyDto.builder()
                .fullName(req.getParameter("name"))
                .code(req.getParameter("code"))
                .sign(req.getParameter("sign"))
                .build();
    }
}
