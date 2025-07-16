package com.localcoupon.couponservice.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.couponservice.auth.dto.response.LogoutResponseDto;
import com.localcoupon.couponservice.auth.service.AuthService;
import com.localcoupon.couponservice.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
//@WebMvcTest(controllers = AuthController.class) 스프링 컨트롤러 빈을 컨텍스트에 등록 의존성 자동 주입
class AuthControllerTest {
    @Mock
    private AuthService authService;
    //Mockmvc 정의
    private MockMvc mockMvc;

    //실제 객체 사용
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        MockitoAnnotations.openMocks(this);
        AuthController authcontroller = new AuthController(authService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(authcontroller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();
    }

    @Test
    @DisplayName("로그인 API")
    void login() throws Exception {
        //given
        LoginRequestDto requestDto = new LoginRequestDto("test@example.com", "password123");

        //응답 객체
        LoginResponseDto responseDto = new LoginResponseDto("abcd-1234-token");
        when(authService.login(any(LoginRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(document("auth-login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.token").description("세션 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("로그아웃 API 문서화")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void logout() throws Exception {
        // given
        String token = "abcd-1234";
        when(authService.logout(token)).thenReturn(new LogoutResponseDto(token));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "SESSION:abcd-1234"))
                .andExpect(status().isOk())
                .andDo(document("auth-logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data").description("응답 데이터 null").optional()
                        )
                ));
    }
}
