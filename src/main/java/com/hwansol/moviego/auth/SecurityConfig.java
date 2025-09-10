package com.hwansol.moviego.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwansol.moviego.member.service.OAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    public static final Map<HttpMethod, String[]> ALLOWED_URLS = new HashMap<>();

    static {
        ALLOWED_URLS.put(HttpMethod.GET, new String[]{
                "/h2-console/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/oauth2/authorization/kakao",
                "/api/members/member/id/{userId}",
                "/api/members/member/email/{userEmail}",
                "/api/members/member/id",
                "/api/members/member/pw",
                "/api/genres",
                "/api/movies/movie",
                "/api/movies",
                "/api/movie-schedules/movie-schedule",
                "/api/movie-schedules"
        });

        ALLOWED_URLS.put(HttpMethod.POST, new String[]{
                "/h2-console/**",
                "/api/members/member/signup",
                "/api/members/member/signin",
                "/api/members/member/auth",
                "/api/members/member/auth-check"
        });
    }

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers(HttpMethod.GET, ALLOWED_URLS.get(HttpMethod.GET)).permitAll()
                        .requestMatchers(HttpMethod.POST, ALLOWED_URLS.get(HttpMethod.POST)).permitAll()
                        .anyRequest().authenticated()
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
                        .accessDeniedHandler(new JwtAccessDeniedHandler(objectMapper))
                ) // 401, 403 에러 핸들러
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headersConfigurer -> headersConfigurer.frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(
                List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(
                List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "New-AccessToken"));
        corsConfiguration.setExposedHeaders(List.of("New-AccessToken"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(86400L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
