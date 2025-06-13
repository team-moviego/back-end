package com.hwansol.moviego.member.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@SQLRestriction("del_date IS NULL")
public class Member extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId; // 회원아이디

    @Column(nullable = false)
    private String userPw; // 회원 비밀번호

    @Column(nullable = false)
    private String userEmail; // 회원 이메일

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role; // 권한

    @Column
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider; // 소셜 로그인 사용자 구분

    @Column
    private LocalDateTime delDate; // 삭제날짜


}
