package com.localcoupon.couponservice.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localcoupon.couponservice.auth.filter.AuthFilter;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import com.localcoupon.couponservice.store.service.StoreService;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = StoreController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthFilter.class)
        })
public class StoreControllerTest {

    @MockBean
    private StoreService storeService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.objectMapper.findAndRegisterModules();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();
    }

    @Test
    @DisplayName("매장 등록 API")
    void registerStore() throws Exception {
        StoreRequestDto requestDto = new StoreRequestDto("스타벅스", "서울시 강남구", StoreCategory.CAFE);
        StoreResponseDto responseDto = new StoreResponseDto(
                1L,
                "매장이름",
                "서울시 강남구",
                "카페",
                "110105",
                37.4979,
                127.0276,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                LocalDateTime.of(2025, 6, 1, 10, 0)
        );

        when(storeService.registerStore(any(StoreRequestDto.class))).thenReturn(responseDto);

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
                                fieldWithPath("category").description("카테고리")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.id").description("매장 ID"),
                                fieldWithPath("data.name").description("매장 이름"),
                                fieldWithPath("data.address").description("주소"),
                                fieldWithPath("data.category").description("카테고리"),
                                fieldWithPath("data.regionCode").description("지역 코드"),
                                fieldWithPath("data.latitude").description("위도"),
                                fieldWithPath("data.longitude").description("경도"),
                                fieldWithPath("data.createdAt").description("생성 시각 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data.updatedAt").description("수정 시각 (yyyy-MM-ddTHH:mm:ss)")
                        )
                ));
    }

    @Test
    @DisplayName("내 매장 목록 조회 API")
    void getMyStores() throws Exception {
        StoreResponseDto responseDto = new StoreResponseDto(
                1L,
                "매장이름",
                "서울시 강남구",
                "카페",
                "110105",
                37.4979,
                127.0276,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                LocalDateTime.of(2025, 6, 1, 10, 0)
        );
        when(storeService.getMyStores()).thenReturn(List.of(responseDto));

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
                                fieldWithPath("data[].regionCode").description("지역 코드"),
                                fieldWithPath("data[].latitude").description("위도"),
                                fieldWithPath("data[].longitude").description("경도"),
                                fieldWithPath("data[].createdAt").description("생성 시각 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data[].updatedAt").description("수정 시각 (yyyy-MM-ddTHH:mm:ss)")
                        )
                ));
    }
}
