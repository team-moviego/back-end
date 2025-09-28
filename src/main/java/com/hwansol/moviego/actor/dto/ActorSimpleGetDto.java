package com.hwansol.moviego.actor.dto;

import com.hwansol.moviego.actor.model.Actor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ActorSimpleGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String name;

        public Response(Long id, String name) {
            boolean isValidateDataFail = id == null || id <= 0 || name == null || name.isBlank();

            if (isValidateDataFail) {
                throw new IllegalArgumentException("ActorSimpleGetDto.Response 생성 실패");
            }

            this.id = id;
            this.name = name;
        }

        public static ActorSimpleGetDto.Response from(Actor actor) {
            return ActorSimpleGetDto.Response.builder()
                    .id(actor.getId())
                    .name(actor.getName())
                    .build();
        }
    }
}
