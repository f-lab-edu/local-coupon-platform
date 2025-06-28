package com.localcoupon.couponservice.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.filter.AuthFilter;
import com.localcoupon.couponservice.auth.service.AuthService;
import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.dto.response.UserResponseDto;
import com.localcoupon.couponservice.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthFilter.class)
        })
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test@example.com", // Principal (userEmail)
                        null,
                        List.of(new SimpleGrantedAuthority("USER_ROLE")) // 권한
                )
        );
    }

    @Test
    @DisplayName("회원가입 API 문서화")
    void createUser() throws Exception {
        SignUpRequestDto requestDto = new SignUpRequestDto("test@example.com", "password123", "tester", "서울시", "110105");
        UserResponseDto responseDto = new UserResponseDto("test@example.com", "tester", "서울시", "110105");

        when(userService.signUpUser(any(SignUpRequestDto.class))).thenReturn(responseDto);

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(document("user-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("address").description("주소"),
                                fieldWithPath("regionCode").description("지역코드")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.address").description("주소"),
                                fieldWithPath("data.regionCode").description("지역 코드")
                        )
                ));
    }

    @Test
    @DisplayName("내 정보 조회 API 문서화")
    void getUser() throws Exception {
        String email = "test@example.com";
        UserResponseDto responseDto = new UserResponseDto(email, "tester", "서울시", "110105");

        when(userService.getUserByEmail(any())).thenReturn(responseDto);

       mockMvc.perform(get("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer abcd1234")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("user-get-me",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.address").description("주소"),
                                fieldWithPath("data.regionCode").description("지역 코드")
                        )
                ));

    }
}
