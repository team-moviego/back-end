package com.hwansol.moviego.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.hwansol.moviego.member.model.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원 조회 관련 dto
public class MemberGetDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class Response {

        @NotBlank(message = "아이디를 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "아이디는 영문 + 숫자 조합으로 작성해야 합니다.")
        private String userId;

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일 형식을 입력해주세요.")
        private String userEmail;

        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdAt;

        public static MemberGetDto.Response from(Member member) {
            return Response.builder()
                .userId(member.getUserId())
                .userEmail(member.getUserEmail())
                .createdAt(member.getCreatedAt())
                .build();
        }
    }
}
