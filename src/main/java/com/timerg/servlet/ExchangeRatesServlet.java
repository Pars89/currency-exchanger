package com.timerg.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timerg.dto.CreateExchangeRateDto;
import com.timerg.dto.ReadCurrencyDto;
import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.service.ExchangeRatesService;
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
        List<ReadExchangeRateDto> exchangeRatesServiceAll = exchangeRatesService.findAll();

        String ans = objectMapper.writeValueAsString(exchangeRatesServiceAll);

        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(ans);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        Optional<ReadExchangeRateDto> readExchangeRateDto = exchangeRatesService.create(
                CreateExchangeRateDto.builder()
                        .baseCurrencyId(req.getParameter("baseCurrencyCode"))
                        .targetCurrencyId(req.getParameter("targetCurrencyCode"))
                        .rate(req.getParameter("rate"))
                        .build()
        );

        String ans = "";
        if (readExchangeRateDto.isPresent()) {
            ans = objectMapper.writeValueAsString(readExchangeRateDto.get());
        } else {
            ans = "null";
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(ans);
        }
    }
}
