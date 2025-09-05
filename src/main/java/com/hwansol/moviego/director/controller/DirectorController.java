package com.hwansol.moviego.director.controller;

import com.hwansol.moviego.director.dto.DirectorSimpleGetDto;
import com.hwansol.moviego.director.service.DirectorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
}
