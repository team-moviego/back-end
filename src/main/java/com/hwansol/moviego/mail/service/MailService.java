package com.hwansol.moviego.mail.service;

import com.hwansol.moviego.mail.exception.MailErrorCode;
import com.hwansol.moviego.mail.exception.MailException;
import com.hwansol.moviego.redis.service.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {


    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    @Value("${spring.mail.sender-email}")
    private String sender; // 메일 보내는 사람 아이디

    /**
     * 아이디 찾기 이메일 발송
     *
     * @param userEmail 받는 이메일
     * @param userId    회원 아이디
     */
    public void sendIdMail(String userEmail, String userId) {
        sendEmail(userEmail, userId, MailType.ID);
    }

    /**
     * 비밀번호 찾기 이메일 발송
     *
     * @param userEmail   받는 이메일
     * @param temporaryPw 임시 비밀번호
     */
    public void sendPwMail(String userEmail, String temporaryPw) {
        sendEmail(userEmail, temporaryPw, MailType.PW);
    }

    /**
     * 인증번호 이메일 발송
     *
     * @param userEmail 받는 이메일
     * @param authNum   인증 번호
     */
    public void sendAuthMail(String userEmail, String authNum) {
        sendEmail(userEmail, authNum, MailType.AUTH);
    }

    // 이메일 전송
    private void sendEmail(String userEmail, String content, MailType mailType) {
        MimeMessage message = createMail(userEmail, content, mailType);
        javaMailSender.send(message);
    }

    // 이메일 생성
    private MimeMessage createMail(String userEmail, String content, MailType mailType) {
        MimeMessage message = javaMailSender.createMimeMessage();

        String subject = setSubject(mailType);
        String body = setBody(mailType, content);

        try {
            message.setFrom(sender);
            message.setRecipients(RecipientType.TO, userEmail);
            message.setSubject(subject);
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            log.error("메일 전송 실패 - {}", LocalDateTime.now());
            log.error("to : {}", userEmail);

            throw new MailException(MailErrorCode.FAIL_SEND_MAIL);
        }

        return message;
    }

    // 메일 제목
    private String setSubject(MailType mailType) {
        if (mailType.equals(MailType.ID)) {
            return "[무비고] 회원님의 아이디 찾기 결과입니다.";
        }

        if (mailType.equals(MailType.PW)) {
            return "[무비고] 회원님의 비밀번호 찾기 결과입니다.";
        }

        return "[무비고] 회원가입을 위한 인증번호 메일입니다.";
    }

    // 메일 내용
    private String setBody(MailType mailType, String content) {
        String body = "";

        if (mailType.equals(MailType.ID)) {
            body += "<h3>회원님의 아이디 찾기 결과입니다.</h3>";
            body += "<h1>" + content + "</h1>";
            body += "<h3>감사합니다.</h3>";
            return body;
        }

        if (mailType.equals(MailType.PW)) {
            body += "<h3>회원님의 임시비밀번호입니다.</h3>";
            body += "<h1>" + content + "</h1>";
            body += "<h3>위 비밀번호로 로그인 이후 비밀번호 변경을 진행해주세요.</h3>";
            body += "<h3>감사합니다.</h3>";
            return body;
        }

        body += "<h3>회원가입을 위한 인증번호입니다.</h3>";
        body += "<h1>" + content + "</h1>";
        body += "<h3>감사합니다.</h3>";
        return body;
    }
}
