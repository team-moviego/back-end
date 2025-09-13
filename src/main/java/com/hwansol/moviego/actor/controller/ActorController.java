package com.hwansol.moviego.actor.controller;

import com.hwansol.moviego.actor.dto.ActorCreateDto;
import com.hwansol.moviego.actor.dto.ActorSimpleGetDto;
import com.hwansol.moviego.actor.dto.ActorUpdateNameDto;
import com.hwansol.moviego.actor.service.ActorService;
import com.hwansol.moviego.common.dto.CommonDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
@Validated
public class ActorController {

    private final ActorService actorService;

    /**
     * 배우 전체 리스트 조회 컨트롤러
     * 관리자만 조회 가능
     *
     * @return 성공 시 200 코드와 조회된 배우 리스트의 순수 정보를 담은 response dto 리스트, 실패 시 에러코드와 에러메시지
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ActorSimpleGetDto.Response>> getActorListController() {
        List<ActorSimpleGetDto.Response> response = actorService.getActorList();

        return ResponseEntity.ok(response);
    }

    /**
     * 배우 생성 컨트롤러
     * 관리자만 생성 가능
     *
     * @param request 배우 생성을 위한 배우명 정보를 담고 있는 request dto
     * @return 성공 시 201 코드와 생성된 엔티티의 pk 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/actor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> createActorController(@Valid @RequestBody
    ActorCreateDto.Request request) {
        CommonDto.Response response = actorService.createActor(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 배우명 변경 컨트롤러
     * 관리자만 변경 가능
     *
     * @param actorId 배우명을 변경할 엔티티 pk
     * @param request 변경할 배우명 정보를 담고 있는 request dto
     * @return 성공 시 200 코드와 배우명이 변경된 엔티티의 pk 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @PatchMapping("/actor/{actorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> updateActorNameController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @PathVariable Long actorId,
            @Valid @RequestBody
            ActorUpdateNameDto.Request request) {
        CommonDto.Response response = actorService.updateActorName(actorId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 배우 영구 삭제 컨트롤러
     *
     * @param actorId 영구 삭제할 엔티티의 pk
     * @param request 영구 삭제를 위한 문구 정보를 담고 있는 request dto
     * @return 성공 시 200 코드와 삭제된 엔티티의 pk 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @DeleteMapping("/actor/{actorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> deleteActorController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @PathVariable Long actorId,
            @Valid @RequestBody CommonDto.DeleteRequest request) {
        CommonDto.Response response = actorService.deleteActor(actorId, request);

        return ResponseEntity.ok(response);
    }
}
