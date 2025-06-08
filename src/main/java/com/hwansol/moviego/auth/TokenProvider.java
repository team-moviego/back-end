package com.hwansol.moviego.auth;

import com.hwansol.moviego.member.service.MemberDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private static final String KEY_ROLES = "roles";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1hour
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 24 * 1000 * 60 * 60; // 24hour
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String COOKIE_NAME = "refreshToken";

    private final MemberDetailsService memberDetailsService;

    @Value("${spring.jwt.secret}")
    private String secret;

    /**
     * accessToken 생성
     *
     * @param memberId 회원 아이디
     * @param roles    회원 권한
     * @return 생성된 accessToken
     */
    public String generateAccessToken(String memberId, List<String> roles) {
        return createToken(memberId, roles, ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     * refreshToken 생성 및 쿠키에 저장
     *
     * @param memberId            회원 아이디
     * @param roles               회원 권한
     * @param httpServletResponse HttpServletResponse
     */
    public void generateRefreshToken(String memberId, List<String> roles,
        HttpServletResponse httpServletResponse) {
        String refreshToken = createToken(memberId, roles, REFRESH_TOKEN_EXPIRE_TIME);
        tokenToCookie(refreshToken, httpServletResponse);
    }

    /**
     * accessToken 재발급
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 재발급된 accessToken
     */
    public String reGenerateAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);

        if (!validateToken(refreshToken)) { // refreshToken이 만료된 경우
            throw new TokenException(TokenErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String memberId = getMemberId(refreshToken);
        List<String> roles = getMemberRole(refreshToken);

        generateRefreshToken(memberId, roles, response); // 새로운 refreshToken 발급 후 쿠키에 저장

        return generateAccessToken(memberId, roles); // 새로운 accessToken 발급
    }

    /**
     * 로그아웃
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        deleteRefreshToken(request, response); // refreshToken을 쿠키에서 지움
    }

    /**
     * jwt를 통해 회원 인증 정보를 가져온다.
     *
     * @param jwt
     * @return 회원의 인증 정보
     */
    @Transactional
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = memberDetailsService.loadUserByUsername(getMemberId(jwt));

        return new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
    }

    /**
     * 토큰에서 회원 아이디를 가져온다.
     *
     * @param token 토큰
     * @return 회원 아이디
     */
    public String getMemberId(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 회원 권한을 가져온다.
     *
     * @param token 토큰
     * @return 회원 권한
     */
    public List<String> getMemberRole(String token) {
        return List.of(String.valueOf(parseClaims(token).get(KEY_ROLES)));
    }

    /**
     * 헤더 정보의 토큰을 가져온다.
     *
     * @param request HttpServletRequest
     * @return 헤더 정보의 올바르게 토큰이 있을 경우 토큰을 반환하고 아닌 경우 null을 반환
     */
    public String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    /**
     * 토큰이 빈 문자열인지와 만료 여부를 확인한다.
     *
     * @param token 토큰
     * @return 올바른 토큰인 경우 true 아닌 경우 false
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = parseClaims(token);

        return !claims.getExpiration().before(new Date());
    }

    // 토큰 정보 가져오는 메소드
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("토큰 정보 에러 = {}", e.getMessage());
            throw new TokenException(TokenErrorCode.EXPIRED_ACCESS_TOKEN);
        }
    }

    // 토큰 생성하는 메소드
    private String createToken(String memberId, List<String> roles, long tokenExpiredTime) {
        Claims claims = Jwts.claims().setSubject(memberId);
        claims.put(KEY_ROLES, roles);

        Date now = new Date(); // 현재 날짜
        Date expiredDate = new Date(now.getTime() + tokenExpiredTime); // 만료 날짜

        return Jwts.builder()
            .setClaims(claims)
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuedAt(now) // 생성 날짜
            .setExpiration(expiredDate) // 만료 날짜
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    // refreshToken 쿠키에 저장하는 메소드
    private void tokenToCookie(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true); // js 접근 불가
        cookie.setSecure(true); // https 외에 통신 불가
        cookie.setMaxAge(86400); // 24시간 후 만료

        response.addCookie(cookie);
    }

    // 쿠키에서 refreshToken 삭제하는 메소드
    private void deleteRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = findCookie(request);

        cookie.setMaxAge(0); // 바로 만료시킴
        response.addCookie(cookie);
    }

    // 쿠키에 저장된 refreshToken 가져오는 메소드
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = findCookie(request);

        return cookie.getValue();
    }

    // refreshToken 정보가 담긴 쿠키 찾는 메소드
    private Cookie findCookie(HttpServletRequest request) {
        if (request.getCookies() == null) { // refreshToken이 존재하지 않는 경우
            throw new TokenException(TokenErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        Cookie cookie = Arrays.stream(request.getCookies())
            .filter(c -> c.getName().equals(COOKIE_NAME))
            .findAny()
            .orElse(null);

        if (cookie == null) { // 해당 쿠키가 존재하지 않는 경우
            throw new TokenException(TokenErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        return cookie;
    }
}
