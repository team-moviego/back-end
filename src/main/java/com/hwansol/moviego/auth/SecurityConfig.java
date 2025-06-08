package com.hwansol.moviego.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                .requestMatchers(HttpMethod.GET, "/api/members/member").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/members/member/signout").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/members/member/email").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/members/member/pw").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/members/member").authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(exception -> exception.authenticationEntryPoint(
                    new JwtAuthenticationEntryPoint(objectMapper))
                .accessDeniedHandler(new JwtAuthDeniedHandler(objectMapper))
            ) // 401, 403 에러 핸들러
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headersConfigurer -> headersConfigurer.frameOptions(
                HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
}
