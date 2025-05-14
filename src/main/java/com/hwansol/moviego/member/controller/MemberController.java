package com.hwansol.moviego.member.controller;

import com.hwansol.moviego.member.dto.MemberFindIdDto;
import com.hwansol.moviego.member.dto.MemberFindIdDto.Response;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * 이메일 중복 확인 컨트롤러
     *
     * @param userEmail 회원 이메일
     * @return 성공 시 200 상태코드와 성공 메시지, 실패 시 에러코드와 에러 메시지
     */
    @GetMapping("/member/{userEmail}")
    public ResponseEntity<String> isDuplicatedEmail(@PathVariable String userEmail) {
        memberService.duplicatedEmail(userEmail);

        return ResponseEntity.ok("사용가능한 이메일입니다.");
    }

    /**
     * 아이디 찾기 컨트롤러
     *
     * @param request MemberFindDto.Request
     * @return MemberFindDto.Response
     */
    @GetMapping("/member/id")
    public ResponseEntity<MemberFindIdDto.Response> findIdController(
        @RequestBody MemberFindIdDto.Request request) {
        Member member = memberService.findId(request);

        Response response = Response.from(member);

        return ResponseEntity.ok(response);
    }
}
