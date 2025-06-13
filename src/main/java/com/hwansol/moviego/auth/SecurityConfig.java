package com.hwansol.moviego.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwansol.moviego.member.service.OAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
        return web -> web.ignoring()
            .requestMatchers("/error", "/favicon.ico");
    }

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

            // oauth2 설정
            .oauth2Login(oauth ->
                oauth.userInfoEndpoint(c -> c.userService(oAuth2UserService))
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler((request, response, exception) -> {
                        log.error("OAuth2 로그인 실패", exception);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "OAuth2 로그인 실패: " + exception.getMessage());
                    }))

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
