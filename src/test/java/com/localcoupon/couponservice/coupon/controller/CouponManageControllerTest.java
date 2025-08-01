package com.localcoupon.couponservice.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.security.CustomUserDetails;
import com.localcoupon.couponservice.common.TestSecurityConfig;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import com.localcoupon.couponservice.common.interceptor.RateLimitInterceptor;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponVerifyRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.ListCouponResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import com.localcoupon.couponservice.store.dto.StoreResponseFields;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import com.localcoupon.couponservice.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CouponManageController.class)
@Import(TestSecurityConfig.class)
class CouponManageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponManageService couponManageService;

    @MockitoBean
    private RateLimitInterceptor interceptor;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) throws IOException {
        when(interceptor.preHandle(any(), any(), any())).thenReturn(true);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();

        // CustomUserDetails 직접 SecurityContext에 넣어주기
        CustomUserDetails userDetails = new CustomUserDetails(
                1L,
                "test@naver.com",
                "password",
                List.of(new SimpleGrantedAuthority(UserRole.ROLE_USER.name()))
        );
        TestingAuthenticationToken auth = new TestingAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("신규 쿠폰을 등록한다. 200")
    @WithMockUser(username = "test@naver.com")
    void createCoupon() throws Exception {
        CouponCreateRequestDto requestDto = new CouponCreateRequestDto(
                "봄맞이 할인",
                "봄 시즌 한정 쿠폰입니다.",
                CouponScope.NATIONAL,
                100,
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0)),
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0))
        );

        StoreRequestDto request = new StoreRequestDto(
                "스타벅스",
                "서울시 송파구 법원로 55",
                StoreCategory.CAFE,
                "02-1234-5678",
                "매장 설명입니다.",
                "http://example.com/image.jpg"
        );


        CouponResponseDto responseDto = new CouponResponseDto(
                1L,
                "봄맞이 할인",
                "봄 시즌 한정 쿠폰입니다.",
                CouponScope.NATIONAL,
                100,
                0,
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0)),
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0))
        );

        when(couponManageService.createCoupon(any(CouponCreateRequestDto.class), any(Long.class))).thenReturn(responseDto);

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(ApiMapping.COUPON_MANAGE_BASE + "/coupons")
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
                                fieldWithPath("validPeriod.start").description("쿠폰 유효 시작일"),
                                fieldWithPath("validPeriod.end").description("쿠폰 유효 종료일"),
                                fieldWithPath("issuePeriod.start").description("쿠폰 발급 시작일"),
                                fieldWithPath("issuePeriod.end").description("쿠폰 발급 종료일")
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
                                fieldWithPath("data.validPeriod.start").description("쿠폰 유효 시작일"),
                                fieldWithPath("data.validPeriod.end").description("쿠폰 유효 종료일"),
                                fieldWithPath("data.issuePeriod.start").description("쿠폰 발급 시작일"),
                                fieldWithPath("data.issuePeriod.end").description("쿠폰 발급 종료일")
                        )
                ));
    }
    @Test
    @DisplayName("내가 등록한 쿠폰 목록을 조회 한다. 200")
    @WithMockUser(username = "test@naver.com")
    void getCoupons() throws Exception {
        CursorPageRequest cursor = CursorPageRequest.of(1L, null, null, null);

        StoreRequestDto request = new StoreRequestDto(
                "스타벅스",
                "서울시 송파구 법원로 55",
                StoreCategory.CAFE,
                "02-1234-5678",
                "매장 설명입니다.",
                "http://example.com/image.jpg"
        );

        Store store = Store.from(
                request,
                new KakaoGeocodeInfoDto(
                        "10010",
                        new BigDecimal("36.2323"),
                        new BigDecimal("126.3232")
                ),
                1L
        );
        CouponCreateRequestDto requestDto = new CouponCreateRequestDto(
                "봄맞이 할인",
                "봄 시즌 한정 쿠폰입니다.",
                CouponScope.NATIONAL,
                100,
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0)),
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0))
        );

        ListCouponResponseDto responseDto = ListCouponResponseDto.from(List.of(Coupon.from(requestDto, store)));

        when(couponManageService.getCouponsByOwner(any(Long.class), any(CursorPageRequest.class))).thenReturn(responseDto);


        mockMvc.perform(get(ApiMapping.COUPON_MANAGE_BASE + "/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cursor", "1")
                        .param("size", "10")
                        .param("sortBy", "asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("coupons-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("cursor").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sortBy").description("정렬 방식")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.couponResponseDtos[].id").description("쿠폰 ID"),
                                fieldWithPath("data.couponResponseDtos[].title").description("쿠폰 제목"),
                                fieldWithPath("data.couponResponseDtos[].description").description("쿠폰 설명"),
                                fieldWithPath("data.couponResponseDtos[].scope").description("쿠폰 범위"),
                                fieldWithPath("data.couponResponseDtos[].totalCount").description("총 발급 가능 수량"),
                                fieldWithPath("data.couponResponseDtos[].issuedCount").description("현재까지 발급된 수량"),
                                fieldWithPath("data.couponResponseDtos[].validPeriod.start").description("쿠폰 유효 시작일"),
                                fieldWithPath("data.couponResponseDtos[].validPeriod.end").description("쿠폰 유효 종료일"),
                                fieldWithPath("data.couponResponseDtos[].issuePeriod.start").description("쿠폰 발급 시작일"),
                                fieldWithPath("data.couponResponseDtos[].issuePeriod.end").description("쿠폰 발급 종료일")
                        ).and(StoreResponseFields.storeResponseFields("data.storeResponse."))
                ));
    }

    @Test
    @DisplayName("쿠폰 상세 조회 200")
    void getCouponDetail() throws Exception {
        // Given
        StoreRequestDto request = new StoreRequestDto(
                "스타벅스",
                "서울시 송파구 법원로 55",
                StoreCategory.CAFE,
                "02-1234-5678",
                "매장 설명입니다.",
                "http://example.com/image.jpg"
        );

        Store store = Store.from(
                request,
                new KakaoGeocodeInfoDto(
                        "10010",
                        new BigDecimal("36.2323"),
                        new BigDecimal("126.3232")
                ),
                1L
        );

        Long couponId = 1L;
        CouponResponseDto responseDto = new CouponResponseDto(
                couponId, "봄맞이 할인", "봄 시즌 한정 쿠폰입니다.",
                CouponScope.NATIONAL, 100, 0,
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0)),
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0))
        );
        when(couponManageService.getCouponDetail(couponId)).thenReturn(responseDto);

        mockMvc.perform(get(ApiMapping.COUPON_MANAGE_BASE + "/coupons/{couponId}", couponId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("coupon-get-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.id").description("쿠폰 ID"),
                                fieldWithPath("data..title").description("쿠폰 제목"),
                                fieldWithPath("data..description").description("쿠폰 설명"),
                                fieldWithPath("data..scope").description("쿠폰 범위 (LOCAL 또는 NATIONAL)"),
                                fieldWithPath("data..totalCount").description("총 발급 가능 수량"),
                                fieldWithPath("data..issuedCount").description("현재까지 발급된 수량"),
                                fieldWithPath("data..validPeriod.start").description("쿠폰 유효 시작일"),
                                fieldWithPath("data..validPeriod.end").description("쿠폰 유효 종료일"),
                                fieldWithPath("data..issuePeriod.start").description("쿠폰 발급 시작일"),
                                fieldWithPath("data..issuePeriod.end").description("쿠폰 발급 종료일")
                        )
                ));
    }
    @Test
    @DisplayName("쿠폰의 이름과 기간을 수정한다.")
    void updateCoupon() throws Exception {
        StoreRequestDto request = new StoreRequestDto(
                "스타벅스",
                "서울시 송파구 법원로 55",
                StoreCategory.CAFE,
                "02-1234-5678",
                "매장 설명입니다.",
                "http://example.com/image.jpg"
        );

        Store store = Store.from(
                request,
                new KakaoGeocodeInfoDto(
                        "10010",
                        new BigDecimal("36.2323"),
                        new BigDecimal("126.3232")
                ),
                1L
        );
        Long couponId = 1L;
        CouponUpdateRequestDto requestDto = new CouponUpdateRequestDto("봄맞이 업데이트", "쿠폰 설명 수정", null, null,
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0)),
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0)));

        CouponResponseDto responseDto = new CouponResponseDto(
                couponId, "봄맞이 업데이트", "쿠폰 설명 수정",
                CouponScope.NATIONAL, 100, 0,
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0)),
                new CouponPeriod(LocalDateTime.of(2025, 7, 1, 0, 0),
                        LocalDateTime.of(2025, 7, 2, 0, 0))
        );



        when(couponManageService.updateCoupon(eq(couponId), any(Long.class), any(CouponUpdateRequestDto.class))).thenReturn(responseDto);

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(patch(ApiMapping.COUPON_MANAGE_BASE + "/coupons/{couponId}", couponId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(document("coupon-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("쿠폰 제목"),
                                fieldWithPath("description").description("쿠폰 설명"),
                                fieldWithPath("totalCount").description("총 발급 가능 수량"),
                                fieldWithPath("scope").description("쿠폰 범위"),
                                fieldWithPath("validPeriod.start").description("쿠폰 유효 시작일"),
                                fieldWithPath("validPeriod.end").description("쿠폰 유효 종료일"),
                                fieldWithPath("issuePeriod.start").description("쿠폰 발급 시작일"),
                                fieldWithPath("issuePeriod.end").description("쿠폰 발급 종료일")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.id").description("쿠폰 ID"),
                                fieldWithPath("data.title").description("쿠폰 제목"),
                                fieldWithPath("data.description").description("쿠폰 설명"),
                                fieldWithPath("data.scope").description("쿠폰 범위"),
                                fieldWithPath("data.totalCount").description("총 발급 가능 수량"),
                                fieldWithPath("data.issuedCount").description("현재까지 발급된 수량"),
                                fieldWithPath("data.validPeriod.start").description("쿠폰 유효 시작일"),
                                fieldWithPath("data.validPeriod.end").description("쿠폰 유효 종료일"),
                                fieldWithPath("data.issuePeriod.start").description("쿠폰 발급 시작일"),
                                fieldWithPath("data.issuePeriod.end").description("쿠폰 발급 종료일")
                        )
                ));
    }

    @Test
    @DisplayName("쿠폰을 삭제처리한다.")
    void deleteCoupon() throws Exception {
        Long couponId = 1L;
        when(couponManageService.deleteCoupon(couponId, 1L)).thenReturn((Result.SUCCESS));

        mockMvc.perform(delete(ApiMapping.COUPON_MANAGE_BASE + "/coupons/{couponId}", couponId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("coupon-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                ));
    }

    @Test
    @DisplayName("쿠폰 인증을 성공적으로 200")
    void verifyCoupon() throws Exception {
        CouponVerifyRequestDto requestDto = new CouponVerifyRequestDto("QR_TOKEN");
        CouponVerifyResponseDto responseDto = new CouponVerifyResponseDto(1L, true);

        when(couponManageService.verifyCoupon(eq("QR_TOKEN"), eq(1L))).thenReturn(responseDto);

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(ApiMapping.COUPON_MANAGE_BASE + "/coupons/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(document("coupon-verify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("qrToken").description("쿠폰 인증을 위한 QR 코드 토큰")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.couponId").description("발급된 쿠폰 ID (1)"),
                                fieldWithPath("data.verified").description("true/false")

                        )
                ));
    }

}