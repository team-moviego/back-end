package com.hwansol.moviego.mail.service;

import com.hwansol.moviego.mail.exception.MailErrorCode;
import com.hwansol.moviego.mail.exception.MailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.sender-email}")
    private String sender; // 메일 보내는 사람 아이디

    /**
     * 이메일 전송
     *
     * @param userEmail - 받을 사람 이메일 주소
     * @param content   - 회원에게 알려줄 내용
     * @param mailType  - 어떤 형식의 메일인지 알려주는 enum
     */
    public void sendEmail(String userEmail, String content, MailType mailType) {
        MimeMessage message = createMail(userEmail, content, mailType);
        javaMailSender.send(message);
    }

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
            e.printStackTrace();
            throw new MailException(MailErrorCode.FAIL_SEND_MAIL);
        }

        return message;
    }

    private String setSubject(MailType mailType) {
        if (mailType.equals(MailType.ID)) {
            return "[무비고] 회원님의 아이디 찾기 결과입니다.";
        }

        if (mailType.equals(MailType.PW)) {
            return "[무비고] 회원님의 비밀번호 찾기 결과입니다.";
        }

        return "[무비고] 회원가입을 위한 인증번호 메일입니다.";
    }

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
