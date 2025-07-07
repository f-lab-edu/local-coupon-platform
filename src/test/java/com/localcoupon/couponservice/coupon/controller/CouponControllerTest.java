package com.localcoupon.couponservice.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.filter.AuthFilter;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CouponController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthFilter.class)
        })
class CouponControllerTest {

    @MockBean
    private CouponService couponService;

    @MockBean
    private StringRedisTemplate redisTemplate;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();
    }

    @Test
    @DisplayName("쿠폰 목록 조회 API 문서화")
    void getAvailableCoupons() throws Exception {
        List<CouponResponseDto> coupons = List.of(
                new CouponResponseDto(
                        1L,
                        "여름 할인",
                        "시원한 여름 쿠폰",
                        CouponScope.LOCAL,
                        100,
                        45,
                        LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 31, 23, 59),
                        "스타벅스 강남점"
                )
        );

        when(couponService.getAvailableCoupons(any(), any())).thenReturn(coupons);

        mockMvc.perform(get("/api/v1/coupons")
                        .param("lat", "37.5665")
                        .param("lng", "126.9780")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("coupon-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data[].id").description("쿠폰 ID"),
                                fieldWithPath("data[].title").description("쿠폰 제목"),
                                fieldWithPath("data[].description").description("쿠폰 설명"),
                                fieldWithPath("data[].scope").description("쿠폰 범위 (LOCAL 또는 NATIONAL)"),
                                fieldWithPath("data[].totalCount").description("총 발급 가능 수량"),
                                fieldWithPath("data[].issuedCount").description("현재까지 발급된 수량"),
                                fieldWithPath("data[].couponValidStartTime").description("쿠폰 유효 시작일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data[].couponValidEndTime").description("쿠폰 유효 종료일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data[].storeName").description("제공 매장 이름")
                        )
                ));
    }

    @Test
    @DisplayName("쿠폰 발급 API 문서화")
    void issueCoupon() throws Exception {
        UserIssuedCouponResponseDto responseDto = new UserIssuedCouponResponseDto(
                1L,
                "여름 할인",
                "여름 맞이 10% 할인",
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 31, 23, 59),
                false,
                "ABC123",
                "qr-token-uuid"
        );

        when(couponService.issueCoupon(1L)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/coupons/{couponId}/issue", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("coupon-issue",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("couponId").description("발급할 쿠폰 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.id").description("발급된 쿠폰 ID"),
                                fieldWithPath("data.couponTitle").description("쿠폰 제목"),
                                fieldWithPath("data.description").description("쿠폰 설명"),
                                fieldWithPath("data.issuedAt").description("발급 일시 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data.couponValidStartTime").description("유효 시작일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data.couponValidEndTime").description("유효 종료일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data.isUsed").description("사용 여부 (true: 사용됨, false: 미사용)"),
                                fieldWithPath("data.displayCode").description("화면 인증 코드 (6자리)"),
                                fieldWithPath("data.qrToken").description("QR 코드 검증용 토큰")
                        )
                ));
    }
}
