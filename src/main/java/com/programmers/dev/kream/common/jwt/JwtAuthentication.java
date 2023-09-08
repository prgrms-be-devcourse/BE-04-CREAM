package com.programmers.dev.kream.common.jwt;


public record JwtAuthentication(String username, String accessToken, String refreshToken) {
}
