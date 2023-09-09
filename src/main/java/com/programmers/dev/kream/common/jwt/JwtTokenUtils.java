package com.programmers.dev.kream.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtTokenUtils {
    public static String getId(String token, String key) {
        return extractClaims(token, key).get("id", String.class);
    }

    public static String gerRole(String token, String key) {
        return extractClaims(token, key).get("role", String.class);
    }

    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();

        return expiredDate.before(new Date());
    }

    private static Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey(key))
                .build()
                .parseClaimsJws(token).getBody();
    }

    public static String generateAccessToken(String id, String role, String key, long expiredTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(getKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(String key, long expiredTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("uuid", UUID.randomUUID().toString());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(getKey(key), SignatureAlgorithm.HS256)
                .compact();
    }


    private static Key getKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }
}
