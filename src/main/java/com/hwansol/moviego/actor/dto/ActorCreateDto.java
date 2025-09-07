package com.hwansol.moviego.actor.dto;

import com.hwansol.moviego.actor.model.Actor;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ActorCreateDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "배우명을 입력해주세요.")
        private String name;

        public Request(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("ActorCreateDto.Request 생성 실패");
            }

            this.name = name;
        }

        public Actor toEntity() {
            return Actor.builder()
                    .name(this.name)
                    .build();
        }
    }
}
