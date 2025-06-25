package com.localcoupon.couponservice.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.couponservice.auth.dto.response.LogoutResponseDto;
import com.localcoupon.couponservice.auth.interceptor.AuthInterceptor;
import com.localcoupon.couponservice.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AuthController.class)
@Import(AuthInterceptor.class)
class AuthControllerTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthInterceptor authInterceptor;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();
    }

    @Test
    @DisplayName("로그인 API")
    void login() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("test@example.com", "password123");
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
