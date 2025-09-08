package com.hwansol.moviego.actor.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ActorUpdateNameDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        private String newName;

        public Request(String newName) {
            if (newName == null || newName.isBlank()) {
                throw new IllegalArgumentException("ActorUpdateNameDto.Request 생성 실패");
            }

            this.newName = newName;
        }
    }
}
