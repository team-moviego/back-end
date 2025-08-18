package com.hwansol.moviego.redis.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hwansol.moviego.redis.exception.RedisErrorCode;
import com.hwansol.moviego.redis.exception.RedisException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @BeforeEach
    void set() throws NoSuchFieldException, IllegalAccessException {
        setPrivate(redisService);
    }

    @Test
    @DisplayName("레디스에 저장된 리프레시 토큰 조회")
    void getRefreshTokenFromRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh:test@naver.com")).thenReturn("refresh");

        String refreshToken = redisService.getRefreshTokenFromRedis("test@naver.com");

        assertThat(refreshToken).isEqualTo("refresh");
    }

    @Test
    @DisplayName("레디스에 저장된 리프레시 토큰 조회 실패 - 존재하지 않음")
    void getRefreshTokenFromRedisFail1() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh:test")).thenReturn(null);

        assertThrows(RedisException.class, () -> redisService.getRefreshTokenFromRedis("test"), RedisErrorCode.NOT_EXIST_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("레디스에 블랙리스트로 등록된 리프레시 토큰 조회")
    void getBlackRefreshTokenFromRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("black:test")).thenReturn("refresh");

        String blackedRefreshToken = redisService.getBlackRefreshTokenFromRedis("test");

        assertThat(blackedRefreshToken).isEqualTo("refresh");
    }

    @Test
    @DisplayName("레디스에 블랙리스트로 등록된 리프레시 토큰 조회 실패 - 존재하지 않음")
    void getBlackRefreshTokenFromRedisFail1() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("black:test")).thenReturn(null);

        assertThrows(RedisException.class, () -> redisService.getBlackRefreshTokenFromRedis("test"), RedisErrorCode.NOT_EXIST_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("레디스에 저장된 인증번호 조회")
    void getAuthNumFromRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("auth:test@naver.com")).thenReturn("1234");

        String authNum = redisService.getAuthNumFromRedis("test@naver.com");

        assertThat(authNum).isEqualTo("1234");
    }

    @Test
    @DisplayName("레디스에 저장된 인증번호 조회 실패 - 존재하지 않음")
    void getAuthNumFromRedisFail1() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("auth:test@naver.com")).thenReturn(null);

        assertThrows(RedisException.class, () -> redisService.getAuthNumFromRedis("test@naver.com"), RedisErrorCode.NOT_EXIST_AUTH.getMessage());
    }

    @Test
    @DisplayName("레디스에 저장된 이메일 인증 여부 조회")
    void getIsAuthFromRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("isAuth:test@naver.com")).thenReturn("false");

        String isAuth = redisService.getIsAuthFromRedis("test@naver.com");

        assertThat(isAuth).isEqualTo("false");
    }

    @Test
    @DisplayName("레디스에 저장된 이메일 인증 여부 조회 실패 - 존재하지 않음")
    void getIsAuthFromRedisFail1() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("isAuth:test@naver.com")).thenReturn(null);

        assertThrows(RedisException.class, () -> redisService.getIsAuthFromRedis("test@naver.com"), RedisErrorCode.NOT_EXIST_AUTH.getMessage());
    }

    @Test
    @DisplayName("레디스에 리프레시 토큰 저장")
    void setRefreshTokenToRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisService.setRefreshTokenToRedis("test", "refresh");

        verify(valueOperations, times(1)).set("refresh:test", "refresh", 86400000L, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("레디스에 인증번호 저장")
    void setAuthNumToRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisService.setAuthNumToRedis("test@naver.com", "1234");

        verify(valueOperations, times(1)).set("auth:test@naver.com", "1234", 1800000L, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("레디스에 메일 인증 여부 저장")
    void setIsAuthToRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisService.setIsAuthToRedis("test@naver.com", "false");

        verify(valueOperations, times(1)).set("isAuth:test@naver.com", "false", 1800000L, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("레디스에 저장된 리프레시 토큰 제거")
    void deleteRefreshTokenFromRedis() {
        when(redisTemplate.hasKey("refresh:test")).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh:test")).thenReturn("refresh");

        redisService.deleteRefreshTokenFromRedis("test");

        verify(redisTemplate, times(1)).delete("refresh:test");
        verify(valueOperations, times(1)).set("black:refresh", "black", 86400000L, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("레디스에 저장된 인증번호 제거")
    void deleteAuthNumFromRedis() {
        when(redisTemplate.hasKey("auth:test@naver.com")).thenReturn(true);

        redisService.deleteAuthNumFromRedis("test@naver.com");

        verify(redisTemplate, times(1)).delete("auth:test@naver.com");
    }

    @Test
    @DisplayName("레디스에 저장된 이메일 인증 여부 제거")
    void deleteIsAuthFromRedis() {
        when(redisTemplate.hasKey("isAuth:test@naver.com")).thenReturn(true);

        redisService.deleteIsAuthFromRedis("test@naver.com");

        verify(redisTemplate, times(1)).delete("isAuth:test@naver.com");
    }

    private void setPrivate(RedisService redisService) throws NoSuchFieldException,
                                                              IllegalAccessException {
        Field refreshTokenExpire = RedisService.class.getDeclaredField("refreshTokenExpire");
        Field authExpire = RedisService.class.getDeclaredField("authExpire");

        refreshTokenExpire.setAccessible(true);
        authExpire.setAccessible(true);

        refreshTokenExpire.set(redisService, 86400000L);
        authExpire.set(redisService, 1800000L);
    }
}