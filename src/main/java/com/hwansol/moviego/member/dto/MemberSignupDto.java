package com.hwansol.moviego.member.dto;

import com.hwansol.moviego.member.model.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원가입 관련 dto
public class MemberSignupDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "아이디를 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "아이디는 영문 + 숫자 조합으로 작성해야 합니다.")
        private String userId;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^[^가-힣ㄱ-ㅎㅏ-ㅣ]+$", message = "비밀번호는 영문 또는 특수문자 또는 숫자만 작성 가능합니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이로 작성하여야합니다.")
        private String userPw;

        @NotBlank(message = "비밀번호 확인란을 입력해주세요.")
        private String confirmPw;

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일 형식을 입력해주세요.")
        private String userEmail;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class Response {

        private String userId;

        public static MemberSignupDto.Response from(Member member) {
            return Response.builder()
                .userId(member.getUserId())
                .build();
        }
    }

}
