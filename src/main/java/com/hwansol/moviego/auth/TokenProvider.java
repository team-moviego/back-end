package com.hwansol.moviego.auth;

import com.hwansol.moviego.auth.exception.TokenErrorCode;
import com.hwansol.moviego.auth.exception.TokenException;
import com.hwansol.moviego.cookie.service.CookieService;
import com.hwansol.moviego.member.service.MemberDetailsService;
import com.hwansol.moviego.redis.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final MemberDetailsService memberDetailsService;
    private final CookieService cookieService;
    private final RedisService redisService;

    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.access.expire}")
    private long accessTokenExpire;

    @Value("${spring.jwt.refresh.expire}")
    private long refreshTokenExpire;

    /**
     * accessToken 생성
     *
     * @param memberId 회원 아이디
     * @param roles    회원 권한
     * @return 생성된 accessToken
     */
    public String generateAccessToken(String memberId, List<String> roles) {
        return createToken(memberId, roles, accessTokenExpire);
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
        String refreshToken = createToken(memberId, roles, refreshTokenExpire);
        cookieService.setCookieToHttpResponse(httpServletResponse, refreshToken, refreshTokenExpire);
        redisService.setRefreshTokenToRedis(memberId, refreshToken);
    }

    /**
     * 로그아웃
     *
     * @param userId   회원 아이디
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public void logout(String userId, HttpServletRequest request, HttpServletResponse response) {
        Cookie refreshTokenCookie = cookieService.getRefreshTokenCookie(request);
        String refreshToken = refreshTokenCookie.getValue();

        cookieService.deleteRefreshTokenCookie(request, response); // refreshToken을 쿠키에서 지움
        redisService.deleteRefreshTokenFromRedis(userId);

        SecurityContextHolder.clearContext();
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

    // 서명할 시크릿 키
    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 정보 가져오는 메소드
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenErrorCode.EXPIRED_TOKEN);
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
                .signWith(getSecretKey())
                .compact();
    }
}
