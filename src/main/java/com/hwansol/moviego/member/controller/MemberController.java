package com.hwansol.moviego.member.controller;

import com.hwansol.moviego.member.dto.MemberAuthDto;
import com.hwansol.moviego.member.dto.MemberFindIdDto;
import com.hwansol.moviego.member.dto.MemberFindIdDto.Response;
import com.hwansol.moviego.member.dto.MemberGetDto;
import com.hwansol.moviego.member.dto.MemberSignInDto;
import com.hwansol.moviego.member.dto.MemberSignupDto;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.model.MemberDetails;
import com.hwansol.moviego.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<String> isDuplicatedId(
        @NotBlank(message = "아이디를 입력해주세요.") @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "아이디는 영문 + 숫자 조합으로 작성해야 합니다.") @PathVariable String userId) {
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
    public ResponseEntity<String> isDuplicatedEmail(
        @NotBlank(message = "아이디를 입력해주세요.") @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일 형식을 입력해주세요.") @PathVariable String userEmail) {
        memberService.duplicatedEmail(userEmail);

        return ResponseEntity.ok("사용가능한 이메일입니다.");
    }

    /**
     * 아이디 찾기 컨트롤러
     *
     * @param request MemberFindDto.Request
     * @return 성공 시 200 코드와 응답 json, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/member/id")
    public ResponseEntity<MemberFindIdDto.Response> findIdController(
        @Valid @RequestBody MemberFindIdDto.Request request) {
        Member member = memberService.findId(request);

        Response response = Response.from(member);

        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 찾기 컨트롤러
     *
     * @param userId    회원 아이디
     * @param userEmail 회원 이메일
     * @return 성공 시 200 코드와 성공 메시지, 실패 시 에러 코드와 에러 메시지
     */
    @GetMapping("/member")
    public ResponseEntity<String> findPwController(
        @NotBlank(message = "아이디를 입력해주세요.") @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "아이디는 영문 + 숫자 조합으로 작성해야 합니다.") @RequestParam String userId,
        @NotBlank(message = "아이디를 입력해주세요.") @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일 형식을 입력해주세요.") @RequestParam String userEmail) {
        memberService.findPw(userId, userEmail);

        return ResponseEntity.ok("이메일로 임시 비밀번호를 발급하였습니다. 로그인 이후 비밀번호 변경 바랍니다.");
    }

    /**
     * 회원 조회 컨트롤러
     *
     * @param memberDetails UserDetails
     * @return 성공 시 200 코드와 응답 json, 실패 시 에러코드와 에러메시지
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/member")
    public ResponseEntity<MemberGetDto.Response> getMemberController(@AuthenticationPrincipal
    MemberDetails memberDetails) {
        String userId = memberDetails.getUsername();

        Member member = memberService.getMember(userId);
        MemberGetDto.Response response = MemberGetDto.Response.from(member);

        return ResponseEntity.ok(response);
    }

    /**
     * 인증번호 이메일 발송 컨트롤러
     *
     * @param userEmail 회원 이메일
     * @return 성공 시 200 코드와 성공 메시지, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/auth")
    public ResponseEntity<String> sendAuthNumController(
        @NotBlank(message = "아이디를 입력해주세요.") @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일 형식을 입력해주세요.") @RequestParam String userEmail) {
        memberService.sendAuthNum(userEmail);

        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

    /**
     * 인증번호 확인 컨트롤러
     *
     * @param request MemberAuthDto.Request
     * @return 성공 시 200 코드와 성공 메시지, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/member/auth-check")
    public ResponseEntity<String> checkAuthNum(@Valid @RequestBody MemberAuthDto.Request request) {
        memberService.checkAuthNum(request);

        return ResponseEntity.ok("이메일 인증에 성공하셨습니다.");
    }

    /**
     * 회원가입 컨트롤러
     *
     * @param request MemberSignupDto.Request
     * @return 성공 시 201 코드와 회원가입한 아이디, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/member/signup")
    public ResponseEntity<MemberSignupDto.Response> signupController(
        @Valid @RequestBody MemberSignupDto.Request request) {
        Member member = memberService.signup(request);
        MemberSignupDto.Response response = MemberSignupDto.Response.from(member);

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(response);
    }

    /**
     * 로그인 컨트롤러
     *
     * @param request         MemberSignInDto.Request
     * @param servletResponse HttpServletResponse
     * @return 성공 시 200 코드와 응답 JSON, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/member/signin")
    public ResponseEntity<MemberSignInDto.Response> signInController(
        @Valid @RequestBody MemberSignInDto.Request request, HttpServletResponse servletResponse) {
        String accessToken = memberService.signIn(request, servletResponse);
        MemberSignInDto.Response response = MemberSignInDto.Response.from(request.getUserId(),
            accessToken);

        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃 컨트롤러
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 성공 시 200 코드와 성공메시지, 실패 시 에러코드와 에러메시지
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/member/signout")
    public ResponseEntity<String> signOutController(HttpServletRequest request,
        HttpServletResponse response) {
        memberService.signOut(request, response);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
