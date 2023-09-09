package com.programmers.dev.kream.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.exception.ErrorCode;
import com.programmers.dev.kream.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(ErrorCode.NO_AUTHORITY));
    }
}
