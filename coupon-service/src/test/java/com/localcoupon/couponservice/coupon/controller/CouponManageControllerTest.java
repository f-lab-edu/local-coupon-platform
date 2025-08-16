package com.localcoupon.couponservice.coupon.controller;

import static com.localcoupon.common.constants.ApiMapping.COUPON_MANAGE_BASE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.CursorPageRequest;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponVerifyRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.ListCouponResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.fixture.CouponFixture;
import com.localcoupon.couponservice.coupon.fixture.StoreResponseFixture;
import com.localcoupon.couponservice.coupon.internal.store.dto.StoreSummaryDto;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import java.time.Clock;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CouponManageController.class)
class CouponManageControllerTest {

  private final Clock clock = Clock.systemDefaultZone();

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private CouponManageService couponManageService;
  @Autowired
  private ObjectMapper objectMapper;

  // ====== 공통 응답(envelope) 필드 도우미 ======
  private static FieldDescriptor[] commonEnvelope(String dataPrefix) {
    return new FieldDescriptor[]{
        fieldWithPath("success").description("요청 성공 여부"),
        fieldWithPath("message").description("응답 메시지"),
        fieldWithPath("httpStatus").description("HTTP 상태 코드")
    };
  }

  @BeforeEach
  void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .apply(
                org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(
                    provider))
            .build();
  }

  @Test
  @DisplayName("신규 쿠폰 등록 - 200 OK")
  @WithMockUser(username = "test@naver.com")
  void createCoupon() throws Exception {
    Coupon base = CouponFixture.activeCoupon(1L, clock);

    var requestDto =
        new CouponCreateRequestDto(
            base.getTitle(),
            base.getDescription(),
            base.getScope(),
            base.getTotalCount(),
            base.getValidPeriod(),
            base.getIssuePeriod());

    var responseDto =
        new CouponResponseDto(
            1L,
            base.getTitle(),
            base.getDescription(),
            base.getScope(),
            base.getTotalCount(),
            base.getIssuedCount(),
            base.getValidPeriod(),
            base.getIssuePeriod());

    given(couponManageService.createCoupon(any(CouponCreateRequestDto.class), any(Long.class)))
        .willReturn(responseDto);

    mockMvc
        .perform(
            post(COUPON_MANAGE_BASE + "/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("X-USER-ID", 1))
        .andExpect(status().isOk())
        .andDo(
            document(
                "coupon-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title").description("쿠폰 제목"),
                    fieldWithPath("description").description("쿠폰 설명"),
                    fieldWithPath("scope").description("쿠폰 범위 (LOCAL | NATIONAL)"),
                    fieldWithPath("totalCount").description("총 발급 가능 수량"),
                    fieldWithPath("validPeriod.start").description("쿠폰 유효 시작일"),
                    fieldWithPath("validPeriod.end").description("쿠폰 유효 종료일"),
                    fieldWithPath("issuePeriod.start").description("쿠폰 발급 시작일"),
                    fieldWithPath("issuePeriod.end").description("쿠폰 발급 종료일")),
                responseFields(commonEnvelope("data.")
                )
                    .and(
                        fieldWithPath("data.id").description("쿠폰 ID"),
                        fieldWithPath("data.title").description("쿠폰 제목"),
                        fieldWithPath("data.description").description("쿠폰 설명"),
                        fieldWithPath("data.scope").description("쿠폰 범위"),
                        fieldWithPath("data.totalCount").description("총 발급 가능 수량"),
                        fieldWithPath("data.issuedCount").description("현재까지 발급된 수량"),
                        fieldWithPath("data.validPeriod.start").description("쿠폰 유효 시작일"),
                        fieldWithPath("data.validPeriod.end").description("쿠폰 유효 종료일"),
                        fieldWithPath("data.issuePeriod.start").description("쿠폰 발급 시작일"),
                        fieldWithPath("data.issuePeriod.end").description("쿠폰 발급 종료일"))));
  }

  @Test
  @DisplayName("내가 등록한 쿠폰 목록 조회 - 200 OK")
  @WithMockUser(username = "test@naver.com")
  void getCoupons() throws Exception {
    Coupon coupon = CouponFixture.activeCoupon(1L, clock);
    var storeSummary = StoreSummaryDto.of(StoreResponseFixture.sample());
    var responseDto = ListCouponResponseDto.from(List.of(coupon), storeSummary);

    given(couponManageService.getCouponsByOwner(any(Long.class), any(CursorPageRequest.class)))
        .willReturn(responseDto);

    mockMvc
        .perform(
            get(COUPON_MANAGE_BASE + "/coupons")
                .header("X-USER-ID", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("cursor", "1")
                .param("size", "10")
                .param("sortBy", "asc")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(
            document(
                "coupons-get",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("cursor").description("페이지 커서 (처음이면 생략 가능)"),
                    parameterWithName("size").description("페이지 크기"),
                    parameterWithName("sortBy").description("정렬 방향 (asc|desc)")),
                responseFields(commonEnvelope("data."))
                    .and(
                        fieldWithPath("data.couponResponseDtos[].id").description("쿠폰 ID"),
                        fieldWithPath("data.couponResponseDtos[].title").description("쿠폰 제목"),
                        fieldWithPath("data.couponResponseDtos[].description").description("쿠폰 설명"),
                        fieldWithPath("data.couponResponseDtos[].scope").description("쿠폰 범위"),
                        fieldWithPath("data.couponResponseDtos[].totalCount").description(
                            "총 발급 가능 수량"),
                        fieldWithPath("data.couponResponseDtos[].issuedCount").description(
                            "현재까지 발급된 수량"),
                        fieldWithPath("data.couponResponseDtos[].validPeriod.start").description(
                            "쿠폰 유효 시작일"),
                        fieldWithPath("data.couponResponseDtos[].validPeriod.end").description(
                            "쿠폰 유효 종료일"),
                        fieldWithPath("data.couponResponseDtos[].issuePeriod.start").description(
                            "쿠폰 발급 시작일"),
                        fieldWithPath("data.couponResponseDtos[].issuePeriod.end").description(
                            "쿠폰 발급 종료일"),
                        // StoreSummary
                        fieldWithPath("data.storeResponse.id").description("가맹점 ID"),
                        fieldWithPath("data.storeResponse.name").description("가맹점 이름"),
                        fieldWithPath("data.storeResponse.address").description("주소"),
                        fieldWithPath("data.storeResponse.category").description("카테고리(enum)"),
                        fieldWithPath("data.storeResponse.latitude").description("위도(BigDecimal)"),
                        fieldWithPath("data.storeResponse.longitude").description(
                            "경도(BigDecimal)"))));
  }

  @Test
  @DisplayName("쿠폰 상세 조회 - 200 OK")
  @WithMockUser(username = "test@naver.com")
  void getCouponDetail() throws Exception {
    Long couponId = 1L;
    Coupon base = CouponFixture.activeCoupon(couponId, clock);

    var responseDto =
        new CouponResponseDto(
            base.getId(),
            base.getTitle(),
            base.getDescription(),
            base.getScope(),
            base.getTotalCount(),
            base.getIssuedCount(),
            base.getValidPeriod(),
            base.getIssuePeriod());

    given(couponManageService.getCouponDetail(couponId)).willReturn(responseDto);

    mockMvc
        .perform(get(COUPON_MANAGE_BASE + "/coupons/{couponId}", couponId)
            .accept(MediaType.APPLICATION_JSON)
            .header("X-USER-ID", 1))
        .andExpect(status().isOk())
        .andDo(
            document(
                "coupon-get-detail",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(commonEnvelope("data."))
                    .and(
                        fieldWithPath("data.id").description("쿠폰 ID"),
                        fieldWithPath("data.title").description("쿠폰 제목"),
                        fieldWithPath("data.description").description("쿠폰 설명"),
                        fieldWithPath("data.scope").description("쿠폰 범위 (LOCAL | NATIONAL)"),
                        fieldWithPath("data.totalCount").description("총 발급 가능 수량"),
                        fieldWithPath("data.issuedCount").description("현재까지 발급된 수량"),
                        fieldWithPath("data.validPeriod.start").description("쿠폰 유효 시작일"),
                        fieldWithPath("data.validPeriod.end").description("쿠폰 유효 종료일"),
                        fieldWithPath("data.issuePeriod.start").description("쿠폰 발급 시작일"),
                        fieldWithPath("data.issuePeriod.end").description("쿠폰 발급 종료일"))));
  }

  @Test
  @DisplayName("쿠폰 수정 - 200 OK")
  @WithMockUser(username = "test@naver.com")
  void updateCoupon() throws Exception {
    Long couponId = 1L;
    Coupon base = CouponFixture.activeCoupon(couponId, clock);

    var requestDto =
        new CouponUpdateRequestDto(
            "봄맞이 업데이트",
            "쿠폰 설명 수정",
            null,
            null,
            base.getValidPeriod(),
            base.getIssuePeriod());

    var responseDto =
        new CouponResponseDto(
            couponId,
            "봄맞이 업데이트",
            "쿠폰 설명 수정",
            base.getScope(),
            base.getTotalCount(),
            base.getIssuedCount(),
            base.getValidPeriod(),
            base.getIssuePeriod());

    given(couponManageService.updateCoupon(eq(couponId), any(Long.class),
        any(CouponUpdateRequestDto.class)))
        .willReturn(responseDto);

    mockMvc
        .perform(
            patch(COUPON_MANAGE_BASE + "/coupons/{couponId}", couponId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("X-USER-ID", 1))
        .andExpect(status().isOk())
        .andDo(
            document(
                "coupon-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title").description("쿠폰 제목"),
                    fieldWithPath("description").description("쿠폰 설명"),
                    fieldWithPath("totalCount").description("총 발급 가능 수량").optional(),
                    fieldWithPath("scope").description("쿠폰 범위").optional(),
                    fieldWithPath("validPeriod.start").description("쿠폰 유효 시작일"),
                    fieldWithPath("validPeriod.end").description("쿠폰 유효 종료일"),
                    fieldWithPath("issuePeriod.start").description("쿠폰 발급 시작일"),
                    fieldWithPath("issuePeriod.end").description("쿠폰 발급 종료일")),
                responseFields(commonEnvelope("data."))
                    .and(
                        fieldWithPath("data.id").description("쿠폰 ID"),
                        fieldWithPath("data.title").description("쿠폰 제목"),
                        fieldWithPath("data.description").description("쿠폰 설명"),
                        fieldWithPath("data.scope").description("쿠폰 범위"),
                        fieldWithPath("data.totalCount").description("총 발급 가능 수량"),
                        fieldWithPath("data.issuedCount").description("현재까지 발급된 수량"),
                        fieldWithPath("data.validPeriod.start").description("쿠폰 유효 시작일"),
                        fieldWithPath("data.validPeriod.end").description("쿠폰 유효 종료일"),
                        fieldWithPath("data.issuePeriod.start").description("쿠폰 발급 시작일"),
                        fieldWithPath("data.issuePeriod.end").description("쿠폰 발급 종료일"))));
  }

  @Test
  @DisplayName("쿠폰 삭제 처리 - 200 OK")
  @WithMockUser(username = "test@naver.com")
  void deleteCoupon() throws Exception {
    Long couponId = 1L;
    given(couponManageService.deleteCoupon(couponId, 1L)).willReturn(Result.SUCCESS);

    mockMvc
        .perform(delete(COUPON_MANAGE_BASE + "/coupons/{couponId}", couponId)
            .accept(MediaType.APPLICATION_JSON)
            .header("X-USER-ID", 1))
        .andExpect(status().isOk())
        .andDo(
            document(
                "coupon-delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(commonEnvelope(null))
                    .and(fieldWithPath("data").description("응답 데이터(성공 시 보통 SUCCESS 등)"))));
  }

  @Test
  @DisplayName("쿠폰 인증 - 200 OK")
  @WithMockUser(username = "test@naver.com")
  void verifyCoupon() throws Exception {
    var requestDto = new CouponVerifyRequestDto("QR_TOKEN");
    var responseDto = new CouponVerifyResponseDto(1L, true);

    given(couponManageService.verifyCoupon(eq("QR_TOKEN"), eq(1L))).willReturn(responseDto);

    mockMvc
        .perform(
            post(COUPON_MANAGE_BASE + "/coupons/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("X-USER-ID", 1))
        .andExpect(status().isOk())
        .andDo(
            document(
                "coupon-verify",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(fieldWithPath("qrToken").description("쿠폰 인증을 위한 QR 코드 토큰")),
                responseFields(commonEnvelope("data."))
                    .and(
                        fieldWithPath("data.couponId").description("발급된 쿠폰 ID"),
                        fieldWithPath("data.verified").description("인증 결과(true/false)"))));
  }
}
