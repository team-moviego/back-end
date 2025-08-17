package com.hwansol.moviego.cookie.service;

import com.hwansol.moviego.cookie.exception.CookieErrorCode;
import com.hwansol.moviego.cookie.exception.CookieException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    /**
     * 리프레시 토큰이 저장된 쿠키 조회
     *
     * @param httpServletRequest HttpServletRequest
     * @return 조회된 쿠키
     */
    public Cookie getRefreshTokenCookie(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            throw new CookieException(CookieErrorCode.NOT_FOUND_COOKIE);
        }

        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .orElseThrow(() -> new CookieException(CookieErrorCode.NOT_FOUND_COOKIE));
    }

    /**
     * 리프레시 토큰을 저장한 쿠키를 httpResponse에 저장
     *
     * @param httpServletResponse HttpServletResponse
     * @param refreshToken        리프레시 토큰
     * @param expire              만료
     */
    public void setCookieToHttpResponse(HttpServletResponse httpServletResponse, String refreshToken, long expire) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .maxAge((int) expire / 1000)
                .path("/")
                .sameSite("strict")
                .httpOnly(true)
                .secure(false)
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * 리프레시 토큰이 저장된 쿠키를 만료시킴
     *
     * @param httpServletRequest  HttpServletRequest
     * @param httpServletResponse HttpServletResponse
     */
    public void deleteRefreshTokenCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Cookie refreshTokenCookie = getRefreshTokenCookie(httpServletRequest);

        refreshTokenCookie.setMaxAge(0);

        httpServletResponse.addCookie(refreshTokenCookie);
    }
}
