package com.hwansol.moviego.member.dto;

import com.hwansol.moviego.member.model.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 이메일 변경 관련 DTO
public class MemberModifyEmailDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {

        @NotBlank(message = "기존 이메일을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일 형식을 입력해주세요.")
        private String originEmail;

        @NotBlank(message = "변경할 이메일을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일 형식을 입력해주세요.")
        private String newEmail;

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class Response {

        private String userEmail;

        public static MemberModifyEmailDto.Response from(Member member) {
            return Response.builder()
                .userEmail(member.getUserEmail())
                .build();
        }
    }

}
