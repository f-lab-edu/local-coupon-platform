package com.localcoupon.couponservice.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localcoupon.couponservice.auth.filter.AuthFilter;
import com.localcoupon.couponservice.auth.security.CustomUserDetails;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import com.localcoupon.couponservice.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.localcoupon.couponservice.store.dto.StoreResponseFields.storeResponseFields;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CouponManageController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthFilter.class)
        })
class CouponManageControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CouponManageService couponManageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.objectMapper.registerModule(new JavaTimeModule());
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
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
    @DisplayName("쿠폰 등록 API 문서화")
    @WithMockUser(username = "test@naver.com")
    void createCoupon() throws Exception {
        CouponCreateRequestDto requestDto = new CouponCreateRequestDto(
                "봄맞이 할인",
                "봄 시즌 한정 쿠폰입니다.",
                CouponScope.NATIONAL,
                100,
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 2, 0, 0),
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 2, 0, 0)
        );

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

        CouponResponseDto responseDto = new CouponResponseDto(
                1L,
                "봄맞이 할인",
                "봄 시즌 한정 쿠폰입니다.",
                CouponScope.NATIONAL,
                100,
                0,
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 2, 0, 0),
                LocalDateTime.of(2025, 7, 1, 0, 0),
                LocalDateTime.of(2025, 7, 2, 0, 0),
                StoreResponseDto.fromEntity(store)
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
                                fieldWithPath("couponValidStartTime").description("쿠폰 유효 시작일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("couponValidEndTime").description("쿠폰 유효 종료일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("couponIssueStartTime").description("쿠폰 발급 시작일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("couponIssueEndTime").description("쿠폰 발급 종료일 (yyyy-MM-ddTHH:mm:ss)")
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
                                fieldWithPath("data.couponIssueStartTime").description("쿠폰 유효 시작일 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data.couponIssueEndTime").description("쿠폰 유효 종료일 (yyyy-MM-ddTHH:mm:ss)")
                        ).and(storeResponseFields("data.storeResponse."))
                ));
    }
}