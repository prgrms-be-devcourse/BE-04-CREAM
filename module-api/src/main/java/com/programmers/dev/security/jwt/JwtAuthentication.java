package com.programmers.dev.security.jwt;


public record JwtAuthentication(String username, String accessToken, String refreshToken) {
}
