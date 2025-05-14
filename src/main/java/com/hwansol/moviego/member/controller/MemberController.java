package com.hwansol.moviego.member.controller;

import com.hwansol.moviego.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    /**
     * 아이디 중복 확인 컨트롤러
     *
     * @param userId 회원 아이디
     * @return 성공 시 200 상태코드와 성공 메시지, 실패 시 에러코드와 에러 메시지
     */
    @GetMapping("/member/{userId}")
    public ResponseEntity<String> isDuplicatedId(@PathVariable String userId) {
        memberService.duplicatedId(userId);

        return ResponseEntity.ok("사용가능한 아이디입니다.");
    }
