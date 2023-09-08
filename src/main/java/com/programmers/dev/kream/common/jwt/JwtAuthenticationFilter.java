package com.programmers.dev.kream.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.exception.ErrorCode;
import com.programmers.dev.kream.exception.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfigure jwtConfigure;

    public JwtAuthenticationFilter(JwtConfigure jwtConfigure) {
        this.jwtConfigure = jwtConfigure;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        }
        else {
            try {
                final String token = header.split(" ")[1].trim();
                JwtTokenUtils.isExpired(token, jwtConfigure.getSecretKey());

                Long userId = Long.parseLong(JwtTokenUtils.getId(token, jwtConfigure.getSecretKey()));
                String userRole = JwtTokenUtils.gerRole(token, jwtConfigure.getSecretKey());

                JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(userRole)));

                jwtAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);

                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException ex) {
                authenticationFailResponse(response, ErrorCode.SESSION_EXPIRATION);

            } catch (Exception ex) {
                authenticationFailResponse(response, ErrorCode.INVALID_SESSION_FORMAT);
            }
        }
    }

    private void authenticationFailResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        new ObjectMapper().writeValue(response.getWriter(), ErrorResponse.of(errorCode));
    }
}
