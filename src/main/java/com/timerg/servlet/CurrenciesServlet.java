package com.timerg.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timerg.dto.CreateCurrencyDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.service.CurrencyService;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<ReadCurrencyDto> currencies = currencyService.findAll();

        String ans = objectMapper.writeValueAsString(currencies);

        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(ans);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        ReadCurrencyDto readCurrencyDto = currencyService.create(CreateCurrencyDto.builder()
                .fullName(req.getParameter("name"))
                .code(req.getParameter("code"))
                .sign(req.getParameter("sign"))
                .build()
        );

        String ans = objectMapper.writeValueAsString(readCurrencyDto);

        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(ans);
        }
    }
}
