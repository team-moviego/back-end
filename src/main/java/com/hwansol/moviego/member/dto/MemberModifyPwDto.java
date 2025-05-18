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

// 회원 비밀번호 변경을 위한 DTO
public class MemberModifyPwDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "기존 비밀번호를 입력해주세요.")
        private String originPw;

        @NotBlank(message = "변경할 비밀번호를 입력해주세요.")
        @Pattern(regexp = "^[^가-힣ㄱ-ㅎㅏ-ㅣ]+$", message = "비밀번호는 영문 또는 특수문자 또는 숫자만 작성 가능합니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이로 작성하여야합니다.")
        private String newPw;

        @NotBlank(message = "변경할 비밀번호를 다시 입력해주세요.")
        private String confirmPw;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class Response {

        private String userId;

        public static MemberModifyPwDto.Response from(Member member) {
            return Response.builder()
                .userId(member.getUserId())
                .build();
        }
    }
}
