package com.programmers.dev.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.security.jwt.*;
import com.programmers.dev.user.application.UserLoginService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableConfigurationProperties(JwtConfigure.class)
public class SecurityConfiguration {

    private final ObjectMapper objectMapper;

    public SecurityConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public JwtAuthenticationProvider authenticationProvider(UserLoginService loginService, JwtConfigure jwtConfigure) {
        return new JwtAuthenticationProvider(loginService, jwtConfigure);
    }

    @Bean
    public AuthenticationManager authenticationManager(JwtAuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtConfigure jwtConfigure) throws Exception {
        http
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/healthcheck")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/user/login")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/user/me")).hasRole("USER")
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/*")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/web/myPage")).permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtConfigure), AnonymousAuthenticationFilter.class)
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.accessDeniedHandler(new JwtAccessDeniedHandler(objectMapper))
                )
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.authenticationEntryPoint(new JwtAuthenticationEntryPoint(objectMapper))
                );

        return http.build();
    }
}
