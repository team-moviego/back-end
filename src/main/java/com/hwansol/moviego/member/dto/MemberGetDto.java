package com.hwansol.moviego.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.hwansol.moviego.member.model.Member;
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
    @Builder(toBuilder = true)
    public static class Response {

        private String userId;
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
