package com.localcoupon.couponservice.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.interceptor.AuthInterceptor;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(UserCouponController.class)
@Import(AuthInterceptor.class)
class UserCouponControllerTest {

    @MockBean
    private UserCouponService userCouponService;

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
    @DisplayName("내 쿠폰 목록 조회")
    void getMyCoupons() throws Exception {
        UserIssuedCouponResponseDto dto = new UserIssuedCouponResponseDto(
                1L,
                "할인 쿠폰",
                "여름 할인 쿠폰입니다.",
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 12, 23, 59),
                false,
                "ABC123",
                "qrTokenValue"
        );
        when(userCouponService.getUserCoupons()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/user-coupons")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("user-coupons-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data[].id").description("발급된 쿠폰 ID"),
                                fieldWithPath("data[].couponTitle").description("쿠폰 제목"),
                                fieldWithPath("data[].description").description("쿠폰 설명"),
                                fieldWithPath("data[].issuedAt").description("발급 시각"),
                                fieldWithPath("data[].couponValidStartTime").description("유효 시작일"),
                                fieldWithPath("data[].couponValidEndTime").description("유효 종료일"),
                                fieldWithPath("data[].isUsed").description("사용 여부"),
                                fieldWithPath("data[].displayCode").description("화면 코드"),
                                fieldWithPath("data[].qrToken").description("QR 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("쿠폰 사용")
    void useCoupon() throws Exception {
        UserIssuedCouponResponseDto dto = new UserIssuedCouponResponseDto(
                1L,
                "할인 쿠폰",
                "여름 할인 쿠폰입니다.",
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 12, 23, 59),
                true,
                "ABC123",
                "qrTokenValue"
        );

        when(userCouponService.useCoupon(1L)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/user-coupons/{id}/use", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("user-coupons-use",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("사용할 쿠폰 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.id").description("쿠폰 ID"),
                                fieldWithPath("data.couponTitle").description("쿠폰 제목"),
                                fieldWithPath("data.description").description("쿠폰 설명"),
                                fieldWithPath("data.issuedAt").description("발급 시각"),
                                fieldWithPath("data.couponValidStartTime").description("유효 시작일"),
                                fieldWithPath("data.couponValidEndTime").description("유효 종료일"),
                                fieldWithPath("data.isUsed").description("사용 여부"),
                                fieldWithPath("data.displayCode").description("화면 코드"),
                                fieldWithPath("data.qrToken").description("QR 토큰")
                        )
                ));
    }
}
