package com.hwansol.moviego.screen.controller;

import com.hwansol.moviego.common.CommonResponseDto;
import com.hwansol.moviego.screen.dto.ScreenCreateDto;
import com.hwansol.moviego.screen.dto.ScreenDeleteDto;
import com.hwansol.moviego.screen.dto.ScreenGetDto;
import com.hwansol.moviego.screen.model.Screen;
import com.hwansol.moviego.screen.service.ScreenService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
@Validated
public class ScreenController {

    private final ScreenService screenService;

    /**
     * 상영관 상세 조회 컨트롤러
     *
     * @param screenId 조회할 상영관 pk (param)
     * @return 성공 시 200 코드와 조회된 상영관 response dto, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/screen")
    public ResponseEntity<ScreenGetDto.Response> getScreenController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @RequestParam Long screenId
                                                                    ) {
        ScreenGetDto.Response response = screenService.getScreen(screenId);

        return ResponseEntity.ok(response);
    }

    /**
     * 전체 상영관 리스트 조회 컨트롤러
     *
     * @return 성공 시 200 코드와 조회된 상영관 리스트 response dto, 실패 시 에러코드와 에러메시지
     */
    @GetMapping
    public ResponseEntity<List<ScreenGetDto.Response>> getScreenListController() {
        List<ScreenGetDto.Response> responseList = screenService.getScreenList();

        return ResponseEntity.ok(responseList);
    }

    /**
     * 상영관 생성 컨트롤러
     * 관리자만 생성 가능
     *
     * @param request ScreenCreateDto.Request
     * @return 성공 시 201 코드와 생성된 상영관 엔티티 pk, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/screen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponseDto> createScreenController(
            @Valid @RequestBody ScreenCreateDto.Request request
                                                                   ) {
        Screen screen = screenService.createScreen(request);
        CommonResponseDto response = CommonResponseDto.from(screen.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 상영관 삭제 컨트롤러
     * 관리자만 가능
     *
     * @param screenId 삭제할 상영관 pk
     * @param request  ScreenDeleteDto.Request
     * @return 성공 시 200 코드와 삭제된 상영관 pk, 실패 시 에러코드와 에러메시지
     */
    @DeleteMapping("/screen/{screenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponseDto> deleteScreenController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @PathVariable Long screenId,
            @Valid @RequestBody ScreenDeleteDto.Request request
                                                                   ) {
        Screen screen = screenService.deleteScreen(screenId, request);
        CommonResponseDto response = CommonResponseDto.from(screen.getId());

        return ResponseEntity.ok(response);
    }
}
