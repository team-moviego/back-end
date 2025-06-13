package com.hwansol.moviego.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        // accessToken, refreshToken 발급
        String accessToken = tokenProvider.generateAccessToken(authentication.getName(),
            roles);
        tokenProvider.generateRefreshToken(authentication.getName(), roles, response);

        // 토큰 전달을 위한 JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> responseData = Map.of(
            "accessToken", accessToken,
            "userId", authentication.getName()
        );

        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }
}
