package com.programmers.dev.kream.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.exception.ErrorCode;
import com.programmers.dev.kream.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(ErrorCode.NO_AUTHENTICATION));
    }
}
