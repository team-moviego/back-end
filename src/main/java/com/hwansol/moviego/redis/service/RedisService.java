package com.hwansol.moviego.redis.service;

import com.hwansol.moviego.redis.exception.RedisErrorCode;
import com.hwansol.moviego.redis.exception.RedisException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh:";
    private static final String BLACK_REFRESH_TOKEN_KEY_PREFIX = "black:";
    private static final String AUTH_NUM_KEY_PREFIX = "auth:";
    private static final String IS_AUTH_KEY_PREFIX = "isAuth:";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.jwt.refresh.expire}")
    private long refreshTokenExpire;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authExpire;

    /**
     * 레디스에 저장된 리프레시 값 조회
     *
     * @param key 키
     * @return 레디스에 저장된 리프레시 토큰
     */
    public String getRefreshTokenFromRedis(String key) {
        String refreshToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + key);
        if (refreshToken == null) {
            throw new RedisException(RedisErrorCode.NOT_EXIST_REFRESH_TOKEN);
        }

        return refreshToken;
    }

    /**
     * 레디스에 블랙리스트로 등록된 리프레시 토큰 조회
     *
     * @param key 키
     * @return 블랙리스트로 등록된 리프레시 토큰
     */
    public String getBlackRefreshTokenFromRedis(String key) {
        String blackRefreshToken = redisTemplate.opsForValue()
                .get(BLACK_REFRESH_TOKEN_KEY_PREFIX + key);
        if (blackRefreshToken == null) {
            throw new RedisException(RedisErrorCode.NOT_EXIST_REFRESH_TOKEN);
        }

        return blackRefreshToken;
    }

    /**
     * 레디스에 저장된 인증번호 조회
     *
     * @param key 키
     * @return 인증번호
     */
    public String getAuthNumFromRedis(String key) {
        String authNum = redisTemplate.opsForValue().get(AUTH_NUM_KEY_PREFIX + key);
        if (authNum == null) {
            throw new RedisException(RedisErrorCode.NOT_EXIST_AUTH);
        }

        return authNum;
    }

    /**
     * 레디스에 저장된 이메일 인증 여부 조회
     *
     * @param key 키
     * @return "true" | "false"
     */
    public String getIsAuthFromRedis(String key) {
        String isAuth = redisTemplate.opsForValue().get(IS_AUTH_KEY_PREFIX + key);
        if (isAuth == null) {
            throw new RedisException(RedisErrorCode.NOT_EXIST_AUTH);
        }

        return isAuth;
    }

    /**
     * 레디스에 리프레시 토큰 저장
     *
     * @param key          키
     * @param refreshToken 리프레시 토큰
     */
    public void setRefreshTokenToRedis(String key, String refreshToken) {
        redisTemplate.opsForValue()
                .set(REFRESH_TOKEN_KEY_PREFIX + key, refreshToken, refreshTokenExpire, TimeUnit.MILLISECONDS);
    }

    /**
     * 레디스에 인증번호 저장
     *
     * @param key     키
     * @param authNum 인증 번호
     */
    public void setAuthNumToRedis(String key, String authNum) {
        redisTemplate.opsForValue()
                .set(AUTH_NUM_KEY_PREFIX + key, authNum, authExpire, TimeUnit.MILLISECONDS);
    }

    /**
     * 레디스에 메일 인증 여부 저장
     *
     * @param key   키
     * @param value 값
     */
    public void setIsAuthToRedis(String key, String value) {
        redisTemplate.opsForValue()
                .set(IS_AUTH_KEY_PREFIX + key, value, authExpire, TimeUnit.MILLISECONDS);
    }

    /**
     * 레디스에 저장된 리프레시 토큰 제거
     *
     * @param key 키
     */
    public void deleteRefreshTokenFromRedis(String key) {
        if (redisTemplate.hasKey(REFRESH_TOKEN_KEY_PREFIX + key)) {
            String refreshToken = getRefreshTokenFromRedis(key);
            redisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + key);

            redisTemplate.opsForValue()
                    .set(BLACK_REFRESH_TOKEN_KEY_PREFIX + refreshToken, "black", refreshTokenExpire, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 레디스에 저장된 인증번호 제거
     *
     * @param key 키
     */
    public void deleteAuthNumFromRedis(String key) {
        if (redisTemplate.hasKey(AUTH_NUM_KEY_PREFIX + key)) {
            redisTemplate.delete(AUTH_NUM_KEY_PREFIX + key);
        }
    }

    /**
     * 레디스에 저장된 이메일 인증 여부 제거
     *
     * @param key 키
     */
    public void deleteIsAuthFromRedis(String key) {
        if (redisTemplate.hasKey(IS_AUTH_KEY_PREFIX + key)) {
            redisTemplate.delete(IS_AUTH_KEY_PREFIX + key);
        }
    }
}
