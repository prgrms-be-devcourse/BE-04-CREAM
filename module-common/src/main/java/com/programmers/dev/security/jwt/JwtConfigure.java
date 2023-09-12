package com.programmers.dev.security.jwt;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "jwt")
public class JwtConfigure {

    private String secretKey;

    private Long accessTokenExpiryTimeMs;

    private Long refreshTokenExpiryTimeMs;

    public String getSecretKey() {
        return secretKey;
    }

    public Long getAccessTokenExpiryTimeMs() {
        return accessTokenExpiryTimeMs;
    }

    public Long getRefreshTokenExpiryTimeMs() {
        return refreshTokenExpiryTimeMs;
    }

    public void setAccessTokenExpiryTimeMs(Long accessTokenExpiryTimeMs) {
        this.accessTokenExpiryTimeMs = accessTokenExpiryTimeMs;
    }

    public void setRefreshTokenExpiryTimeMs(Long refreshTokenExpiryTimeMs) {
        this.refreshTokenExpiryTimeMs = refreshTokenExpiryTimeMs;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
