package com.localcoupon.otherservice.store.controller;

import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.otherservice.common.TestSecurityConfig;
import com.localcoupon.otherservice.fixtures.StoreRequestFixture;
import com.localcoupon.otherservice.store.dto.request.StoreRequestDto;
import com.localcoupon.otherservice.store.dto.request.UserStoreSearchRequestDto;
import com.localcoupon.otherservice.store.dto.response.StoreResponseDto;
import com.localcoupon.otherservice.store.service.StoreService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = StoreController.class)
class StoreControllerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockBean
  private StoreService storeService;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp(RestDocumentationContextProvider provider, WebApplicationContext context) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity())
        .apply(
            org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(
                provider))
        .build();
  }

  @Test
  @DisplayName("매장 등록 API")
  @WithMockUser(username = "test@naver.com")
  void registerStore() throws Exception {
    // given
    StoreRequestDto requestDto = StoreRequestFixture.request();
    StoreResponseDto responseDto = new StoreResponseDto(
        1L, "스타벅스", "서울특별시 송파구 법원로 55",
        com.localcoupon.otherservice.store.enums.StoreCategory.CAFE,
        BigDecimal.valueOf(37.4979), BigDecimal.valueOf(127.0276),
        "02-1234-5678", "커피 전문점",
        "https://cdn.example.com/store.jpg",
        LocalDateTime.of(2025, 6, 1, 10, 0)
    );
    given(storeService.registerStore(any(StoreRequestDto.class), any(Long.class)))
        .willReturn(responseDto);

    // when & then
    mockMvc.perform(post("/stores")
            .with(csrf()) // CSRF 필요 시
            .header("X-USER-ID", 1L) // 컨트롤러가 헤더에서 userId를 읽는 경우
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andDo(document("store-register",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("name").description("매장 이름"),
                fieldWithPath("address").description("주소"),
                fieldWithPath("category").description("카테고리"),
                fieldWithPath("phoneNumber").description("전화번호"),
                fieldWithPath("description").description("매장 설명"),
                fieldWithPath("imageUrl").description("매장 대표 이미지 URL")
            ),
            responseFields(
                fieldWithPath("success").description("요청 성공 여부"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                fieldWithPath("data.id").description("매장 ID"),
                fieldWithPath("data.name").description("매장 이름"),
                fieldWithPath("data.address").description("주소"),
                fieldWithPath("data.category").description("카테고리"),
                fieldWithPath("data.latitude").description("위도"),
                fieldWithPath("data.longitude").description("경도"),
                fieldWithPath("data.phoneNumber").description("전화번호"),
                fieldWithPath("data.description").description("매장 설명"),
                fieldWithPath("data.imageUrl").description("매장 대표 이미지 URL"),
                fieldWithPath("data.createdAt").description("생성 시각 (yyyy-MM-ddTHH:mm:ss)")
            )
        ));
  }

  @Test
  @WithMockUser(username = "test@naver.com")
  @DisplayName("내 매장 목록 조회 API")
  void getMyStores() throws Exception {
    // given
    StoreResponseDto responseDto = new StoreResponseDto(
        1L, "스타벅스", "서울특별시 송파구 법원로 55",
        com.localcoupon.otherservice.store.enums.StoreCategory.CAFE,
        BigDecimal.valueOf(37.4979), BigDecimal.valueOf(127.0276),
        "02-1234-5678", "커피 전문점",
        "https://cdn.example.com/store.jpg",
        LocalDateTime.of(2025, 6, 1, 10, 0)
    );
    // 서비스는 리스트를 반환하도록
    given(storeService.getMyStores(1L)).willReturn(responseDto);

    // when & then
    mockMvc.perform(get("/stores/my")
            .header("X-USER-ID", 1L) // 컨트롤러가 헤더에서 userId 추출 시 필요
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("store-get-my",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("success").description("요청 성공 여부"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                fieldWithPath("data.id").description("매장 ID"),
                fieldWithPath("data.name").description("매장 이름"),
                fieldWithPath("data.address").description("주소"),
                fieldWithPath("data.category").description("카테고리"),
                fieldWithPath("data.latitude").description("위도"),
                fieldWithPath("data.longitude").description("경도"),
                fieldWithPath("data.phoneNumber").description("전화번호"),
                fieldWithPath("data.description").description("매장 설명"),
                fieldWithPath("data.imageUrl").description("매장 대표 이미지 URL"),
                fieldWithPath("data.createdAt").description("생성 시각 (yyyy-MM-ddTHH:mm:ss)")
            )
        ));
  }

  @Test
  @WithMockUser(username = "test@naver.com")
  @DisplayName("근처 매장 조회 API")
  void getStoresNearby() throws Exception {
    // given
    StoreResponseDto responseDto = new StoreResponseDto(
        1L, "스타벅스", "서울특별시 송파구 법원로 55",
        com.localcoupon.otherservice.store.enums.StoreCategory.CAFE,
        BigDecimal.valueOf(37.4979), BigDecimal.valueOf(127.0276),
        "02-1234-5678", "커피 전문점",
        "https://cdn.example.com/store.jpg",
        LocalDateTime.of(2025, 6, 1, 10, 0)
    );
    given(storeService.getStoresNearby(any(UserStoreSearchRequestDto.class)))
        .willReturn(List.of(responseDto));

    // when & then
    mockMvc.perform(get("/stores/nearby")
            .param("minLatitude", "37.48")
            .param("maxLatitude", "37.50")
            .param("minLongitude", "127.01")
            .param("maxLongitude", "127.03")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("store-get-nearby",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("success").description("요청 성공 여부"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                fieldWithPath("data[].id").description("매장 ID"),
                fieldWithPath("data[].name").description("매장 이름"),
                fieldWithPath("data[].address").description("주소"),
                fieldWithPath("data[].category").description("카테고리"),
                fieldWithPath("data[].latitude").description("위도"),
                fieldWithPath("data[].longitude").description("경도"),
                fieldWithPath("data[].phoneNumber").description("전화번호"),
                fieldWithPath("data[].description").description("매장 설명"),
                fieldWithPath("data[].imageUrl").description("매장 대표 이미지 URL"),
                fieldWithPath("data[].createdAt").description("생성 시각 (yyyy-MM-ddTHH:mm:ss)")
            )
        ));
  }

  @Test
  @WithMockUser(username = "test@naver.com")
  @DisplayName("근처 매장 조회 API - 실패 (좌표 검증 오류)")
  void getStoresNearby_validationFail() throws Exception {
    mockMvc.perform(get("/stores/nearby")
            .param("minLatitude", "10.0")  // 잘못된 값
            .param("maxLatitude", "37.50")
            .param("minLongitude", "1333.01") // 잘못된 값
            .param("maxLongitude", "127.03")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(document("store-get-nearby-validation-fail",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("success").description("요청 성공 여부"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                fieldWithPath("errorCode").description("에러 코드"),
                fieldWithPath("detailMessage").description("자세한 오류 메시지")
            )
        ));
  }
}
