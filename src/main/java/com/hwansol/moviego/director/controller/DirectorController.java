package com.hwansol.moviego.director.controller;

import com.hwansol.moviego.common.CommonDto;
import com.hwansol.moviego.director.dto.DirectorCreateDto;
import com.hwansol.moviego.director.dto.DirectorSimpleGetDto;
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
     * 감독 삭제 컨트롤러
     *
     * @param directorId 삭제할 감독 pk
     * @param request
     * @return
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