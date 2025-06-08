package com.hwansol.moviego.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwansol.moviego.auth.SecurityConfig;
import com.hwansol.moviego.auth.TokenProvider;
import com.hwansol.moviego.member.dto.MemberAuthDto.Request;
import com.hwansol.moviego.member.dto.MemberModifyEmailDto;
import com.hwansol.moviego.member.dto.MemberModifyPwDto;
import com.hwansol.moviego.member.dto.MemberSignInDto;
import com.hwansol.moviego.member.dto.MemberSignupDto;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.model.MemberDetails;
import com.hwansol.moviego.member.model.Role;
import com.hwansol.moviego.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({MemberController.class, SecurityConfig.class})
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private MemberService memberService;

    @Test
    @DisplayName("아이디 중복 확인 컨트롤러")
    void isDuplicatedId() throws Exception {
        mockMvc.perform(get("/api/members/member/id/test"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("아이디 중복 확인 컨트롤러 실패 - 올바르지 않은 경로")
    void isDuplicatedIdFail1() throws Exception {
        mockMvc.perform(get("/api/members/member/test"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("아이디 중복 확인 컨트롤러 실패 - 아이디 미입력")
    void isDuplicatedIdFail2() throws Exception {
        mockMvc.perform(get("/api/members/member/id/ "))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아이디 중복 확인 컨트롤러 실패 - 아이디 한글 입력")
    void isDuplicatedIdFail3() throws Exception {
        mockMvc.perform(get("/api/members/member/id/아이디"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 중복 확인 컨트롤러")
    void isDuplicatedEmail() throws Exception {
        mockMvc.perform(get("/api/members/member/email/test@naver.com"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("이메일 중복 확인 컨트롤러 실패 - 존재하지 않는 경로")
    void isDuplicatedEmailFail1() throws Exception {
        mockMvc.perform(get("/api/members/member/test@naver.com"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이메일 중복 확인 컨트롤러 실패 - 이메일 미입력")
    void isDuplicatedEmailFail2() throws Exception {
        mockMvc.perform(get("/api/members/member/email/ "))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 중복 확인 컨트롤러 실패 - 이메일 형식 맞지 않음")
    void isDuplicatedEmailFail3() throws Exception {
        mockMvc.perform(get("/api/members/member/email/test.com"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아이디 찾기 컨트롤러")
    void findIdController() throws Exception {
        Member member = Member.builder()
            .userId("test")
            .userPw("pw")
            .role(Role.ROLE_USER)
            .userEmail("test@naver.com")
            .build();

        when(memberService.findId("test@naver.com")).thenReturn(member);

        mockMvc.perform(get("/api/members/member/id?userEmail=test@naver.com"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userEmail").value("test@naver.com"));
    }

    @Test
    @DisplayName("아이디 찾기 컨트롤러 실패 - 알맞지 않은 경로")
    void findIdControllerFail1() throws Exception {
        mockMvc.perform(get("/api/members/member/ids/"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("아이디 찾기 컨트롤러 실패 - 이메일 미입력")
    void findIdControllerFail2() throws Exception {
        mockMvc.perform(get("/api/members/member/id?userEmail="))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아이디 찾기 컨트롤러 실패 - 이메일 형식 아님")
    void findIdControllerFail3() throws Exception {
        mockMvc.perform(get("/api/members/member/id?userEmail=test.com"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 찾기 컨트롤러")
    void findPwController() throws Exception {
        mockMvc.perform(get("/api/members/member/pw?userId=test&userEmail=test@naver.com"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("비밀번호 찾기 컨트롤러 실패 - 알맞지 않은 경로")
    void findPwControllerFail1() throws Exception {
        mockMvc.perform(get("/api/members/member/pws?userId=test&userEmail=test@naver.com"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("비밀번호 찾기 컨트롤러 실패 - 아이디 미입력")
    void findPwControllerFail2() throws Exception {
        mockMvc.perform(get("/api/members/member/pw?userId=&userEmail=test@naver.com"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 찾기 컨트로러 실패 - 아이디 형식 틀림")
    void findPwControllerFail3() throws Exception {
        mockMvc.perform(get("/api/members/member/pw?userId=테스트&userEmail=test@naver.com"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 찾기 컨트롤러 실패 - 이메일 미입력")
    void findPwControllerFail4() throws Exception {
        mockMvc.perform(get("/api/members/member/pw?userId=test&userEmail="))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 찾기 컨트롤러 실패 - 이메일 형식 맞지 않음")
    void findPwControllerFail5() throws Exception {
        mockMvc.perform(get("/api/members/member/pw?userId=test&userEmail=test.com"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 조회 컨트롤러")
    void getMemberController() throws Exception {
        Member member = Member.builder()
            .userEmail("test@naver.com")
            .role(Role.ROLE_USER)
            .userPw("pw")
            .userId("test")
            .build();
        MemberDetails memberDetails = new MemberDetails(member);

        when(memberService.getMember("test")).thenReturn(member);

        mockMvc.perform(get("/api/members/member")
                .with(user(memberDetails)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("test"))
            .andExpect(jsonPath("$.userEmail").value("test@naver.com"));
    }

    @Test
    @DisplayName("회원 조회 컨트롤러 실패 - 로그인 안 한 경우")
    void getMemberControllerFail1() throws Exception {
        mockMvc.perform(get("/api/members/member"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 조회 컨트롤러 실패 - 없는 경로")
    void getMemberControllerFail2() throws Exception {
        mockMvc.perform(get("/api/members/members"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("인증번호 이메일 발송 컨트롤러")
    void sendAuthNumController() throws Exception {
        mockMvc.perform(post("/api/members/auth?userEmail=test@naver.com"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증번호 이메일 발송 컨트롤러 실패 - 없는 경로")
    void sendAuthNumControllerFail1() throws Exception {
        mockMvc.perform(post("/api/members/auths?userEmail=test@naver.com"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("인증번호 이메일 발송 컨트롤러 실패 - 이메일 미입력")
    void sendAuthNumControllerFail2() throws Exception {
        mockMvc.perform(post("/api/members/auth?userEmail="))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("인증번호 이메일 발송 컨트롤러 실패 - 이메일 형식 맞지 않음")
    void sendAuthNumControllerFail3() throws Exception {
        mockMvc.perform(post("/api/members/auth?userEmail=test.com"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("인증번호 확인 컨트롤러")
    void checkAuthNum() throws Exception {
        Request request = Request.builder()
            .authNum("123456")
            .userEmail("test@naver.com")
            .build();

        mockMvc.perform(post("/api/members/member/auth-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증번호 확인 컨트롤러 실패 - 없는 경로")
    void checkAuthNumFail1() throws Exception {
        mockMvc.perform(post("/api/members/member/auth-checks"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("인증번호 확인 컨트롤러 실패 - 인증번호 미입력")
    void checkAuthNumFail2() throws Exception {
        Request request = Request.builder()
            .userEmail("test@naver.com")
            .build();

        mockMvc.perform(post("/api/members/member/auth-check")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("인증번호 확인 컨트롤러 실패 - 인증번호가 6자리 및 숫자가 아님 않음")
    void checkAuthNumFail3() throws Exception {
        Request request = Request.builder()
            .userEmail("test@naver.com")
            .authNum("dsfd")
            .build();

        mockMvc.perform(post("/api/members/member/auth-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("인증번호 확인 컨트롤러 실패 - 이메일 미입력")
    void checkAuthNumFail4() throws Exception {
        Request request = Request.builder()
            .authNum("123456")
            .build();

        mockMvc.perform(post("/api/members/member/auth-check")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("인증번호 확인 컨트롤러 실패 - 이메일 형식 맞지 않음")
    void checkAuthNumFail5() throws Exception {
        Request request = Request.builder()
            .authNum("123456")
            .userEmail("test.com")
            .build();

        mockMvc.perform(post("/api/members/member/auth-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 컨트롤러")
    void signupController() throws Exception {
        MemberSignupDto.Request request = MemberSignupDto.Request.builder()
            .userId("test")
            .userEmail("test@naver.com")
            .userPw("abc12345")
            .confirmPw("abc12345")
            .build();
        Member member = Member.builder()
            .role(Role.ROLE_USER)
            .userEmail("test@naver.com")
            .userId("test")
            .userPw("abc12345")
            .build();

        when(memberService.signup(argThat(r -> r.getUserId().equals("test")))).thenReturn(member);

        mockMvc.perform(post("/api/members/member/signup")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value("test"));
    }

    @Test
    @DisplayName("회원가입 컨트롤러 실패 - 없는 경로")
    void signupControllerFail1() throws Exception {
        mockMvc.perform(post("/api/members/member/signups"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원가입 컨트롤러 실패 - 아이디 미입력")
    void signupControllerFail2() throws Exception {
        MemberSignupDto.Request request = MemberSignupDto.Request.builder()
            .confirmPw("abc12345")
            .userPw("abc12345")
            .userEmail("test@naver.com")
            .build();

        mockMvc.perform(post("/api/members/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 컨트롤러 실패 - 아이디 형식 맞지 않음")
    void signupControllerFail3() throws Exception {
        MemberSignupDto.Request request = MemberSignupDto.Request.builder()
            .userEmail("test@naver.com")
            .userPw("qwe12345")
            .confirmPw("qwe12345")
            .userId("아이디")
            .build();

        mockMvc.perform(post("/api/members/member/signup")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 컨트롤러 실패 - 이메일 미입력")
    void signupControllerFail4() throws Exception {
        MemberSignupDto.Request request = MemberSignupDto.Request.builder()
            .userId("test")
            .confirmPw("qwe12345")
            .userPw("qwe12345")
            .build();

        mockMvc.perform(post("/api/members/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 컨트롤러 실패 - 이메일 형식 맞지 않음")
    void signupControllerFail5() throws Exception {
        MemberSignupDto.Request request = MemberSignupDto.Request.builder()
            .userId("test")
            .confirmPw("qwe12345")
            .userPw("qwe12345")
            .userEmail("test.com")
            .build();

        mockMvc.perform(post("/api/members/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 컨트롤러 실패 - 비밀번호 미입력")
    void signupControllerFail6() throws Exception {
        MemberSignupDto.Request request = MemberSignupDto.Request.builder()
            .userId("test")
            .confirmPw("qwe12345")
            .userEmail("test.com")
            .build();

        mockMvc.perform(post("/api/members/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 컨트롤러 실패 - 비밀번호 형식 맞지 않음")
    void signupControllerFail7() throws Exception {
        MemberSignupDto.Request request = MemberSignupDto.Request.builder()
            .userId("test")
            .userPw("pa12")
            .confirmPw("pa12")
            .userEmail("test.com")
            .build();

        mockMvc.perform(post("/api/members/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 컨트롤러")
    void signInController() throws Exception {
        MemberSignInDto.Request request = MemberSignInDto.Request.builder()
            .userPw("qwe12345")
            .userId("test")
            .build();

        when(memberService.signIn(argThat(r -> r.getUserId().equals("test")),
            any(HttpServletResponse.class))).thenReturn("token");

        mockMvc.perform(post("/api/members/member/signin")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("test"))
            .andExpect(jsonPath("$.accessToken").value("token"));
    }

    @Test
    @DisplayName("로그인 컨트롤러 실패 - 없는 경로")
    void signInControllerFail1() throws Exception {
        mockMvc.perform(post("/api/members/member/signins"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 컨트롤러 실패 - 아이디 미입력")
    void signInControllerFail2() throws Exception {
        MemberSignInDto.Request request = MemberSignInDto.Request.builder()
            .userPw("qwe12345")
            .build();

        mockMvc.perform(post("/api/members/member/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 컨트롤러 실패 - 아이디 형식 맞지 않음")
    void signInControllerFail3() throws Exception {
        MemberSignInDto.Request request = MemberSignInDto.Request.builder()
            .userPw("qwe12345")
            .userId("아이디")
            .build();

        mockMvc.perform(post("/api/members/member/signin")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 컨트롤러 실패 - 비밀번호 미입력")
    void signInControllerFail4() throws Exception {
        MemberSignInDto.Request request = MemberSignInDto.Request.builder()
            .userId("test")
            .build();

        mockMvc.perform(post("/api/members/member/signin")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 컨트롤러 실패 - 비밀번호 형식 맞지 않음")
    void signInControllerFail5() throws Exception {
        MemberSignInDto.Request request = MemberSignInDto.Request.builder()
            .userId("test")
            .userPw("ㅇㅇou2")
            .build();

        mockMvc.perform(post("/api/members/member/signin")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그아웃 컨트롤러")
    void signOutController() throws Exception {
        Member member = Member.builder()
            .userPw("qwe12345")
            .userId("test")
            .userEmail("test@naver.com")
            .role(Role.ROLE_USER)
            .build();
        MemberDetails memberDetails = new MemberDetails(member);

        mockMvc.perform(post("/api/members/member/signout")
                .with(user(memberDetails)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그아웃 컨트롤러 실패 - 없는 경로")
    void signOutControllerFail1() throws Exception {
        mockMvc.perform(post("/api/members/member/signouts"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그아웃 컨트롤러 실패 - 로그인 하지 않은 상태")
    void signOutControllerFail2() throws Exception {
        mockMvc.perform(post("/api/members/member/signout"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 이메일 변경 컨트롤러")
    void modifyEmailController() throws Exception {
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .newEmail("test@gmail.com")
            .originEmail("test@naver.com")
            .build();
        Member member = Member.builder()
            .role(Role.ROLE_USER)
            .userEmail("test@gmail.com")
            .userId("test")
            .userPw("qwe12345")
            .build();
        MemberDetails memberDetails = new MemberDetails(member);

        when(memberService.modifyEmail(
            argThat(r -> r.getNewEmail().equals("test@gmail.com")))).thenReturn(member);

        mockMvc.perform(patch("/api/members/member/email")
                .with(user(memberDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userEmail").value("test@gmail.com"));
    }

    @Test
    @DisplayName("회원 이메일 변경 컨트롤러 실패 - 없는 경로")
    void modifyEmailControllerFail1() throws Exception {
        mockMvc.perform(patch("/api/members/member/emails"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원 이메일 변경 컨트롤러 실패 - 로그인 하지 않음")
    void modifyEmailControllerFail2() throws Exception {
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .newEmail("test@gmail.com")
            .originEmail("test@naver.com")
            .build();

        mockMvc.perform(patch("/api/members/member/email")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 이메일 변경 컨트롤러 실패 - 이메일 미입력")
    void modifyEmailControllerFail3() throws Exception {
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .originEmail("test@naver.com")
            .build();
        Member member = Member.builder()
            .userPw("qwe12345")
            .userId("test")
            .userEmail("test@gmail.com")
            .role(Role.ROLE_USER)
            .build();
        MemberDetails memberDetails = new MemberDetails(member);

        mockMvc.perform(patch("/api/members/member/email")
                .with(user(memberDetails))
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 이메일 변경 컨트롤러 실패 - 이메일 형식 맞지 않음")
    void modifyEmailControllerFail4() throws Exception {
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .newEmail("test.com")
            .originEmail("test@naver.com")
            .build();
        Member member = Member.builder()
            .userPw("qwe12345")
            .userId("test")
            .userEmail("test@gmail.com")
            .role(Role.ROLE_USER)
            .build();
        MemberDetails memberDetails = new MemberDetails(member);

        mockMvc.perform(patch("/api/members/member/email")
                .with(user(memberDetails))
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 비밀번호 변경 컨트롤러")
    void modifyPwController() throws Exception {
        MemberModifyPwDto.Request request = MemberModifyPwDto.Request.builder()
            .newPw("qwe12345")
            .confirmPw("qwe12345")
            .originPw("asd12345")
            .build();
        Member member = Member.builder()
            .role(Role.ROLE_USER)
            .userEmail("test@naver.com")
            .userId("test")
            .userPw("qwe12355")
            .build();
        MemberDetails memberDetails = new MemberDetails(member);

        when(memberService.modifyPw(eq("test"),
            argThat(r -> r.getNewPw().equals("qwe12345")))).thenReturn(member);

        mockMvc.perform(patch("/api/members/member/pw")
                .with(user(memberDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("test"));
    }

    @Test
    @DisplayName("회원 비밀번호 변경 컨트롤러 실패 - 없는 경로")
    void modifyPwControllerFail1() throws Exception {
        mockMvc.perform(patch("/api/members/member/pws"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원 비밀번호 변경 컨트롤러 실패 - 로그인 하지 않음")
    void modifyPwControllerFail2() throws Exception {
        MemberModifyPwDto.Request request = MemberModifyPwDto.Request.builder()
            .newPw("qwe12345")
            .confirmPw("qwe12345")
            .originPw("asd12345")
            .build();

        mockMvc.perform(patch("/api/members/member/pw")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 비밀번호 변경 컨트롤러 실패 - 비밀번호 미입력")
    void modifyPwControllerFai3() throws Exception {
        MemberModifyPwDto.Request request = MemberModifyPwDto.Request.builder()
            .confirmPw("qwe12345")
            .originPw("asd12345")
            .build();
        Member member = Member.builder()
            .role(Role.ROLE_USER)
            .userEmail("test@naver.com")
            .userId("test")
            .userPw("qwe12355")
            .build();
        MemberDetails memberDetails = new MemberDetails(member);

        mockMvc.perform(patch("/api/members/member/pw")
                .with(user(memberDetails))
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 비밀번호 변경 컨트롤러 실패 - 비밀번호 형식 맞지 않음")
    void modifyPwControllerFai4() throws Exception {
        MemberModifyPwDto.Request request = MemberModifyPwDto.Request.builder()
            .newPw("아2")
            .confirmPw("qwe12345")
            .originPw("asd12345")
            .build();
        Member member = Member.builder()
            .role(Role.ROLE_USER)
            .userEmail("test@naver.com")
            .userId("test")
            .userPw("qwe12355")
            .build();
        MemberDetails memberDetails = new MemberDetails(member);

        mockMvc.perform(patch("/api/members/member/pw")
                .with(user(memberDetails))
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 탈퇴 컨트롤러")
    void deleteMemberController() throws Exception {
        Member member = Member.builder()
            .userPw("qwe12345")
            .userId("test")
            .userEmail("test@naver.com")
            .role(Role.ROLE_USER)
            .build();
        MemberDetails memberDetails = new MemberDetails(member);

        when(memberService.deleteMember(eq("test"), any(HttpServletRequest.class),
            any(HttpServletResponse.class))).thenReturn(member);

        mockMvc.perform(delete("/api/members/member")
                .with(user(memberDetails)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("test"));
    }

    @Test
    @DisplayName("회원 탈퇴 컨트롤러 실패 - 없는 경로")
    void deleteMemberControllerFail1() throws Exception {
        mockMvc.perform(delete("/api/members/members"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원 탈퇴 컨트롤러 실패 - 로그인 하지 않음")
    void deleteMemberControllerFail2() throws Exception {
        mockMvc.perform(delete("/api/members/member"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
}