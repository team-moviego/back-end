package com.hwansol.moviego.mail.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hwansol.moviego.redis.service.RedisService;
import jakarta.mail.internet.MimeMessage;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    void set() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(mailService);
    }

    @Test
    @DisplayName("아이디 찾기 이메일 발송")
    void sendIdMail() {
        mailService.sendIdMail("test@naver.com", "test");

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendPwMail() {
    }

    @Test
    void sendAuthNum() {
    }

    @Test
    void checkAuthNum() {
    }

    private void setPrivateField(MailService mailService) throws NoSuchFieldException,
                                                                 IllegalAccessException {
        Field sender = mailService.getClass().getDeclaredField("sender");
        sender.setAccessible(true);
        sender.set(mailService, "admin@naver.com");
    }
}