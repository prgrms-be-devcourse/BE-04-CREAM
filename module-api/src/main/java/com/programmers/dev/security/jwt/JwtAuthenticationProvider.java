package com.programmers.dev.security.jwt;

import com.programmers.dev.user.application.UserLoginService;
import com.programmers.dev.user.domain.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserLoginService loginService;

    private final JwtConfigure jwtConfigure;

    public JwtAuthenticationProvider(UserLoginService loginService, JwtConfigure jwtConfigure) {
        this.loginService = loginService;
        this.jwtConfigure = jwtConfigure;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken beforeAuthenticationToken = (JwtAuthenticationToken) authentication;

        return processAuthentication(
                String.valueOf(beforeAuthenticationToken.getPrincipal()),
                beforeAuthenticationToken.getCredentials()
        );
    }

    private Authentication processAuthentication(String principal, String credentials) {
        User user = loginService.login(principal, credentials);
        String userRole = user.getUserRole().toString();

        String accessToken = JwtTokenUtils.generateAccessToken(user.getId().toString(), userRole, jwtConfigure.getSecretKey(), jwtConfigure.getAccessTokenExpiryTimeMs());

        return new JwtAuthenticationToken(
                new JwtAuthentication(user.getEmail(), accessToken, "default-refresh-token"),
                credentials,
                List.of(new SimpleGrantedAuthority(userRole))
        );
    }

}
