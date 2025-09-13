package com.hwansol.moviego.director.controller;

import com.hwansol.moviego.common.dto.CommonDto;
import com.hwansol.moviego.director.dto.DirectorCreateDto;
import com.hwansol.moviego.director.dto.DirectorSimpleGetDto;
import com.hwansol.moviego.director.dto.DirectorUpdateNameDto;
import com.hwansol.moviego.director.service.DirectorService;
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
@RequestMapping("/api/directors")
@RequiredArgsConstructor
@Validated
public class DirectorController {

    private final DirectorService directorService;

    /**
     * 감독 리스트 조회 컨트롤러
     *
     * @return 성공 시 200 코드와 조회된 감독 리스트 dto, 실패 시 에러코드와 에러메시지
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DirectorSimpleGetDto.Response>> getDirectorListController() {
        List<DirectorSimpleGetDto.Response> response = directorService.getDirectorList();

        return ResponseEntity.ok(response);
    }

    /**
     * 감독 생성 컨틀롤러
     *
     * @param request 감독 생성에 필요한 이름 필드 정보를 가지고 있는 Request dto 클래스
     * @return 성공 시 201 코드와 생성된 감독 엔티티의 pk 정보를 가지고 있는 dto, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/director")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> createDirectorController(@Valid @RequestBody
    DirectorCreateDto.Request request) {
        CommonDto.Response response = directorService.createDirector(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 감독명 변경 컨트롤러
     * 관리자만 변경 가능
     *
     * @param directorId 감독명을 변경할 엔티티의 pk
     * @param request    변경할 감독명 정보를 담고 있는 request dto
     * @return 성공 시 200 코드와 감독명이 변경된 엔티티의 pk 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @PatchMapping("/director/{directorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> updateDirectorNameController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @PathVariable Long directorId,
            @Valid @RequestBody
            DirectorUpdateNameDto.Request request) {
        CommonDto.Response response = directorService.updateDirectorName(directorId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 감독 제거 컨트롤러
     *
     * @param directorId 제거할 감독 pk
     * @param request    영구 삭제를 위한 문구 정보가 포함된 request dto
     * @return 성공 시 200 코드와 삭제된 감독 엔티티 ㅔk를 담은 response dto, 실패 시 에러코드와 에러메시지
     */
    @DeleteMapping("/director/{directorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> deleteDirectorController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @PathVariable Long directorId,
            @Valid @RequestBody CommonDto.DeleteRequest request) {
        CommonDto.Response response = directorService.deleteDirector(directorId, request);

        return ResponseEntity.ok(response);
    }
}
