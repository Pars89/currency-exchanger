package com.timerg.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.timerg.dto.ReadExchangeRateDto;
import com.timerg.service.ExchangeRatesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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

        String[] strings = req.getRequestURI().split("/");

        String ans = "";

        // parse
        String baseCode = strings[2].substring(0, 3).toUpperCase();
        String targetCode = strings[2].substring(3, 6).toUpperCase();
        Optional<ReadExchangeRateDto> byCode = exchangeRatesService.findByCodes(baseCode, targetCode);

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

        String rate = req.getReader().readLine().split("=")[1];

        String[] strings = req.getRequestURI().split("/");

        String ans = "";

        // parse
        String baseCode = strings[2].substring(0, 3).toUpperCase();
        String targetCode = strings[2].substring(3, 6).toUpperCase();

        Optional<ReadExchangeRateDto> byCode = exchangeRatesService.updateByCodes(baseCode, targetCode, rate);

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
