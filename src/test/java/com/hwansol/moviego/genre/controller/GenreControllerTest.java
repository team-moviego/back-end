package com.hwansol.moviego.genre.controller;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwansol.moviego.auth.OAuth2SuccessHandler;
import com.hwansol.moviego.auth.SecurityConfig;
import com.hwansol.moviego.auth.TokenProvider;
import com.hwansol.moviego.cookie.service.CookieService;
import com.hwansol.moviego.genre.dto.GenreCreateDto;
import com.hwansol.moviego.genre.model.Genre;
import com.hwansol.moviego.genre.service.GenreService;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.model.PrincipalDetails;
import com.hwansol.moviego.member.model.Role;
import com.hwansol.moviego.member.service.OAuth2UserService;
import com.hwansol.moviego.redis.service.RedisService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({SecurityConfig.class, GenreController.class})
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private OAuth2UserService oAuth2UserService;

    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CookieService cookieService;

    @MockitoBean
    private RedisService redisService;

    @Test
    @DisplayName("전체 장르 리스트 조회 컨트롤러")
    void getGenreListController() throws Exception {
        Genre genre = Genre.builder()
                .name("genre")
                .build();
        genre.withId(1L);

        when(genreService.getGenreList()).thenReturn(List.of(genre));

        mockMvc.perform(get("/api/genres"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("장르 생성 컨트롤러")
    void createGenreController() throws Exception {
        Member member = Member.builder()
                .role(Role.ROLE_ADMIN)
                .userEmail("test@naver.com")
                .userId("test")
                .userPw("1234")
                .build();
        PrincipalDetails principalDetails = new PrincipalDetails(member, null);
        GenreCreateDto.Request request = GenreCreateDto.Request.builder()
                .name("genre")
                .build();
        Genre genre = Genre.builder()
                .name("genre")
                .build();
        genre.withId(1L);

        when(genreService.createGenre(argThat(r -> r.getName().equals("genre")))).thenReturn(genre);

        mockMvc.perform(post("/api/genres/genre")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("장르 생성 컨트롤러 실패 - 비로그인 상태")
    void createGenreControllerFail1() throws Exception {
        GenreCreateDto.Request request = GenreCreateDto.Request.builder()
                .name("genre")
                .build();

        mockMvc.perform(post("/api/genres/genre")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("장르 생성 컨트롤러 실패 - 권한 없음")
    void createGenreControllerFail2() throws Exception {
        Member member = Member.builder()
                .role(Role.ROLE_USER)
                .userEmail("test@naver.com")
                .userId("test")
                .userPw("1234")
                .build();
        PrincipalDetails principalDetails = new PrincipalDetails(member, null);
        GenreCreateDto.Request request = GenreCreateDto.Request.builder()
                .name("genre")
                .build();

        mockMvc.perform(post("/api/genres/genre")
                        .with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장르 하드 삭제 컨트롤러")
    void deleteGenreController() throws Exception {
        Member member = Member.builder()
                .role(Role.ROLE_ADMIN)
                .userEmail("test@naver.com")
                .userId("test")
                .userPw("1234")
                .build();
        PrincipalDetails principalDetails = new PrincipalDetails(member, null);
        Genre genre = Genre.builder()
                .name("genre")
                .build();
        genre.withId(1L);

        when(genreService.deleteGenre(1L)).thenReturn(genre);

        mockMvc.perform(delete("/api/genres/genre/1")
                        .with(user(principalDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("장르 하드 삭제 컨트롤러 실패 - pathVariable 유효성 검사 실패")
    void deleteGenreControllerFail1() throws Exception {
        Member member = Member.builder()
                .role(Role.ROLE_ADMIN)
                .userEmail("test@naver.com")
                .userId("test")
                .userPw("1234")
                .build();
        PrincipalDetails principalDetails = new PrincipalDetails(member, null);

        mockMvc.perform(delete("/api/genres/genre/-1")
                        .with(user(principalDetails)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}