package com.hwansol.moviego.auth;

import com.hwansol.moviego.cookie.service.CookieService;
import com.hwansol.moviego.redis.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final TokenProvider tokenProvider;
    private final CookieService cookieService;
    private final RedisService redisService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        if (method.equals("OPTIONS")) {
            return true;
        }

        String[] allowedUrls = SecurityConfig.ALLOWED_URLS.getOrDefault(HttpMethod.valueOf(method), null);
        if (allowedUrls != null) {
            String allowedUrl = Arrays.stream(allowedUrls)
                    .filter(u -> pathMatcher.match(u, requestURI))
                    .findAny()
                    .orElse(null);

            return allowedUrl != null;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = tokenProvider.resolveTokenFromRequest(request);
        if (accessToken != null && tokenProvider.validateToken(accessToken)) { // accessToken 유효한 경우
            setAuthenticated(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        Cookie refreshTokenCookie = cookieService.getRefreshTokenCookie(request);
        String refreshToken = refreshTokenCookie != null ? refreshTokenCookie.getValue() : null;
        if (refreshToken != null && redisService.getBlackRefreshTokenFromRedis(refreshToken) != null) { // 리프레시 토큰이 블랙리스트인 경우
            log.error("블랙리스트 토큰으로 요청 시도로 인한 거절 - {}", LocalDateTime.now());
            filterChain.doFilter(request, response);
            return;
        }

        if (refreshToken != null && tokenProvider.validateToken(refreshToken)) { // 리프레시 토큰이 유효한 경우
            String newAccessToken = regenerateAccessTokenToHeader(response,
                    refreshToken); // 엑세스 토큰 재발급
            if (newAccessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            setAuthenticated(newAccessToken);
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String regenerateAccessTokenToHeader(HttpServletResponse response,
                                                 String refreshToken) {
        String memberId = tokenProvider.getMemberId(refreshToken);
        String refreshTokenOfRedis = redisService.getRefreshTokenFromRedis(memberId);
        if (!refreshToken.equals(refreshTokenOfRedis)) {
            return null;
        }

        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        List<String> roles = authentication.getAuthorities().stream()
                .map(Object::toString)
                .toList();
        String newAccessToken = tokenProvider.generateAccessToken(memberId, roles);
        response.setHeader("New-AccessToken", newAccessToken);

        return newAccessToken;
    }

    private void setAuthenticated(String token) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
