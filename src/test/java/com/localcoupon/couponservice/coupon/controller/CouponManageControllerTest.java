package com.localcoupon.couponservice.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localcoupon.couponservice.auth.interceptor.AuthInterceptor;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponVerifyRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponManageController.class)
@ExtendWith(RestDocumentationExtension.class)
@Import(AuthInterceptor.class)
class CouponManageControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CouponManageService couponManageService;

    @MockBean
    private AuthInterceptor authInterceptor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();
    }

    @Test
    @DisplayName("쿠폰 등록 API 문서화")
    void createCoupon() throws Exception {
        CouponCreateRequestDto requestDto = new CouponCreateRequestDto(
                "봄맞이 할인",
                "봄 시즌 한정 쿠폰입니다.",
                CouponScope.NATIONAL,
                100,
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 2, 0, 0)
        );

        CouponResponseDto responseDto = new CouponResponseDto(
                1L,
                "봄맞이 할인",
                "봄 시즌 한정 쿠폰입니다.",
                CouponScope.NATIONAL,
                100,
                0,
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 2, 0, 0),
                "스타벅스 강남점"
        );

        when(couponManageService.createCoupon(any(CouponCreateRequestDto.class))).thenReturn(responseDto);

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/v1/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(document("coupon-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("쿠폰 제목"),
                                fieldWithPath("description").description("쿠폰 설명"),
                                fieldWithPath("scope").description("쿠폰 범위 (LOCAL 또는 NATIONAL)"),
                                fieldWithPath("totalCount").description("총 발급 가능 수량"),
                                fieldWithPath("couponValidStartTime").description("쿠폰 유효 시작일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("couponValidEndTime").description("쿠폰 유효 종료일 (yyyy-MM-ddTHH:mm:ss)")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.id").description("쿠폰 ID"),
                                fieldWithPath("data.title").description("쿠폰 제목"),
                                fieldWithPath("data.description").description("쿠폰 설명"),
                                fieldWithPath("data.scope").description("쿠폰 범위 (LOCAL 또는 NATIONAL)"),
                                fieldWithPath("data.totalCount").description("총 발급 가능 수량"),
                                fieldWithPath("data.issuedCount").description("현재까지 발급된 수량"),
                                fieldWithPath("data.couponValidStartTime").description("쿠폰 유효 시작일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data.couponValidEndTime").description("쿠폰 유효 종료일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data.storeName").description("쿠폰 제공 매장 이름")
                        )
                ));
    }

    @Test
    @DisplayName("쿠폰 검증(사용 확인) API 문서화")
    void verifyCoupon() throws Exception {
        CouponVerifyRequestDto requestDto = new CouponVerifyRequestDto("1234-uuid-token");
        CouponVerifyResponseDto responseDto = new CouponVerifyResponseDto(10L, true);

        when(couponManageService.verifyCoupon(any(String.class))).thenReturn(responseDto);

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(patch("/api/v1/user-coupons/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(document("coupon-verify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("qrToken").description("쿠폰 검증 토큰 (qrToken)")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.couponId").description("쿠폰 ID"),
                                fieldWithPath("data.verified").description("사용 확인 여부")
                        )
                ));
    }
}