package com.localcoupon.couponservice.coupon.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * user-coupons : 내 쿠폰 목록 조회
 */
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = UserCouponController.class)
class UserCouponControllerTest {

  private static final String USER_ID_HEADER = "X-USER-ID";
  private static final String USER_ID = "1";

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @MockBean(answer = Answers.RETURNS_DEFAULTS)
  private UserCouponService userCouponService;

  /**
   * 공통 envelope(success/message/httpStatus)
   */
  private static FieldDescriptor[] commonEnvelope() {
    return new FieldDescriptor[]{
        fieldWithPath("success").description("요청 성공 여부"),
        fieldWithPath("message").description("응답 메시지"),
        fieldWithPath("httpStatus").description("HTTP 상태 코드")
    };
  }

  /**
   * X-USER-ID 기본 헤더를 요청에 추가
   */
  private static MockHttpServletRequestBuilder withUserId(MockHttpServletRequestBuilder builder) {
    return builder.header(USER_ID_HEADER, USER_ID);
  }

  // ===== helpers =====

  @BeforeEach
  void setUp(RestDocumentationContextProvider provider) throws Exception {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            // 모든 요청에 공통 헤더(X-USER-ID) 적용
            .defaultRequest(withUserId(get("/")))
            .apply(
                org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(
                    provider))
            .build();
  }

  @Test
  @DisplayName("내 쿠폰 목록 조회 - 200 OK")
  @WithMockUser(username = "tester@example.com")
  void getMyCoupons() throws Exception {
    // given
    UserIssuedCouponResponseDto dto = new UserIssuedCouponResponseDto(
        1L,
        "할인 쿠폰",
        "여름 할인 쿠폰입니다.",
        LocalDateTime.of(2025, 7, 1, 0, 0),
        LocalDateTime.of(2025, 7, 1, 0, 0),
        LocalDateTime.of(2025, 7, 12, 23, 59),
        false,
        "ABC123"
    );
    when(userCouponService.getUserCoupons()).thenReturn(List.of(dto));

    // when & then
    mockMvc.perform(withUserId(get("/user-coupons"))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("user-coupons-get",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(commonEnvelope())
                .and(
                    fieldWithPath("data[].id").description("발급된 쿠폰 ID"),
                    fieldWithPath("data[].couponTitle").description("쿠폰 제목"),
                    fieldWithPath("data[].description").description("쿠폰 설명"),
                    fieldWithPath("data[].issuedAt").description("발급 시각"),
                    fieldWithPath("data[].couponValidStartTime").description("유효 시작일"),
                    fieldWithPath("data[].couponValidEndTime").description("유효 종료일"),
                    fieldWithPath("data[].isUsed").description("사용 여부"),
                    fieldWithPath("data[].qrToken").description("QR 토큰")
                )
        ));
  }
}
