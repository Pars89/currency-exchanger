package com.timerg.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String[] strings = req.getRequestURI().split("/");

        String ans = "";


        Optional<ReadExchangeRateDto> byCode = exchangeRatesService.findByCodes(strings[2]);

        if (byCode.isPresent()) {
            ans = objectMapper.writeValueAsString(byCode.get());
        } else {
            ans = "null";
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(ans);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] strings = req.getRequestURI().split("/");

        String ans = "";

        Optional<ReadExchangeRateDto> byCode = exchangeRatesService.updateByCodes(strings[2], req.getParameter("rate"));

        if (byCode.isPresent()) {
            ans = objectMapper.writeValueAsString(byCode.get());
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
