package com.localcoupon.couponservice.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.common.TestSecurityConfig;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.dto.request.UserStoreSearchRequestDto;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import com.localcoupon.couponservice.store.service.StoreService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = StoreController.class)
class StoreControllerTest { ;

    @MockitoBean
    private StoreService storeService;
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider, WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();

//        setSecurityContextHolder();
    }

    @Test
    @DisplayName("매장 등록 API")
    @WithMockUser(username = "test@naver.com")
   void registerStore() throws Exception {
        StoreRequestDto requestDto = new StoreRequestDto(
                "스타벅스",
                "서울특별시 송파구 법원로 55",
                StoreCategory.CAFE,
                "010-1234-5678",
                "커피 전문점",
                "https://cdn.example.com/store.jpg"
        );

        StoreResponseDto responseDto = new StoreResponseDto(
                1L,
                "스타벅스",
                "서울특별시 송파구 법원로 55",
                StoreCategory.CAFE,
                BigDecimal.valueOf(37.4979),
                BigDecimal.valueOf(127.0276),
                "010-1234-5678",
                "커피 전문점",
                "https://cdn.example.com/store.jpg",
                LocalDateTime.of(2025, 6, 1, 10, 0)
        );

        when(storeService.registerStore(any(StoreRequestDto.class), any(Long.class)))
                .thenReturn(responseDto);

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/v1/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
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
        StoreResponseDto responseDto = new StoreResponseDto(
                1L,
                "스타벅스",
                "서울특별시 송파구 법원로 55",
                StoreCategory.CAFE,
                BigDecimal.valueOf(37.4979),
                BigDecimal.valueOf(127.0276),
                "02-1234-5678",
                "커피 전문점",
                "https://cdn.example.com/store.jpg",
                LocalDateTime.of(2025, 6, 1, 10, 0)
        );

        when(storeService.getMyStores(eq(1L)))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/stores/my")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("store-get-my",
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
    @DisplayName("근처 매장 조회 API")
    void getStoresNearby() throws Exception {
        StoreResponseDto responseDto = new StoreResponseDto(
                1L,
                "스타벅스",
                "서울특별시 송파구 법원로 55",
                StoreCategory.CAFE,
                BigDecimal.valueOf(37.4979),
                BigDecimal.valueOf(127.0276),
                "02-1234-5678",
                "커피 전문점",
                "https://cdn.example.com/store.jpg",
                LocalDateTime.of(2025, 6, 1, 10, 0)
        );

        when(storeService.getStoresNearby(any(UserStoreSearchRequestDto.class)))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/stores/nearby")
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
    @DisplayName("근처 매장 조회 API - 실패 (좌표 범위 벗어남)")
    void getStoresNearby_validationFail_latitude() throws Exception {
        mockMvc.perform(get("/api/v1/stores/nearby")
                        .param("minLatitude", "10.0")
                        .param("maxLatitude", "37.50")
                        .param("minLongitude", "1333.01")
                        .param("maxLongitude", "127.03")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(document("store-get-nearby-validation-fail-latitude",
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
