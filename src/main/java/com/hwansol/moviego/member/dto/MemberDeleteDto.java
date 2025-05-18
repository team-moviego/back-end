package com.hwansol.moviego.member.dto;

import com.hwansol.moviego.member.model.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원 탈퇴 관련 DTO
public class MemberDeleteDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class Response {

        private String userId;

        public static MemberDeleteDto.Response from(Member member) {
            return Response.builder()
                .userId(member.getUserId())
                .build();
        }
    }
}
