package com.localcoupon.couponservice.campaign.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localcoupon.couponservice.auth.interceptor.AuthInterceptor;
import com.localcoupon.couponservice.campaign.dto.request.CampaignCreateRequestDto;
import com.localcoupon.couponservice.campaign.dto.response.CampaignResponseDto;
import com.localcoupon.couponservice.campaign.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
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
@WebMvcTest(CampaignController.class)
@Import(AuthInterceptor.class)
public class CampaignControllerTest {

    @MockBean
    private CampaignService campaignService;

    @MockBean
    private AuthInterceptor authInterceptor;

    @MockBean
    private StringRedisTemplate redisTemplate;

    private MockMvc mockMvc;
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
    @DisplayName("캠페인 등록 API 문서화")
    void createCampaign() throws Exception {
        CampaignCreateRequestDto requestDto = new CampaignCreateRequestDto(
                "Summer Sale",
                "할인 이벤트",
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 10),
                List.of(100L)
        );

        CampaignResponseDto responseDto = new CampaignResponseDto(
                1L,
                "Summer Sale",
                "할인 이벤트",
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 10),
                100L,
                "Store1",
                "User1",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of()
        );

        when(campaignService.createCampaign(any(CampaignCreateRequestDto.class))).thenReturn(responseDto);

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(document("campaign-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("캠페인 제목"),
                                fieldWithPath("description").description("캠페인 설명"),
                                fieldWithPath("campaignStartTime").description("시작 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("campaignEndTime").description("종료 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("storeIds").description("연결할 매장 ID 목록")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data.id").description("캠페인 ID"),
                                fieldWithPath("data.title").description("캠페인 제목"),
                                fieldWithPath("data.description").description("캠페인 설명"),
                                fieldWithPath("data.startDate").description("시작 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("data.endDate").description("종료 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("data.storeId").description("매장 ID"),
                                fieldWithPath("data.storeName").description("매장 이름"),
                                fieldWithPath("data.createdBy").description("작성자 이메일"),
                                fieldWithPath("data.createdAt").description("캠페인 생성 일시 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data.updatedAt").description("캠페인 수정 일시 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data.coupons").description("연결된 쿠폰 목록 (비어있을 수 있음)")
                        )
                ));
    }

    @Test
    @DisplayName("캠페인 목록 조회 API 문서화")
    void getCampaigns() throws Exception {
        List<CampaignResponseDto> campaigns = List.of(
                new CampaignResponseDto(
                        1L,
                        "Summer",
                        "Discount",
                        LocalDate.of(2025, 7, 1),
                        LocalDate.of(2025, 7, 10),
                        100L,
                        "스타벅스 강남점",
                        "admin@example.com",
                        LocalDateTime.of(2025, 6, 24, 10, 0),
                        LocalDateTime.of(2025, 6, 24, 10, 0),
                        List.of()
                )
        );

        when(campaignService.getCampaigns()).thenReturn(campaigns);

        mockMvc.perform(get("/api/v1/campaigns"))
                .andExpect(status().isOk())
                .andDo(document("campaign-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("httpStatus").description("HTTP 상태 코드"),
                                fieldWithPath("data[].id").description("캠페인 ID"),
                                fieldWithPath("data[].title").description("캠페인 제목"),
                                fieldWithPath("data[].description").description("캠페인 설명"),
                                fieldWithPath("data[].startDate").description("시작 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("data[].endDate").description("종료 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("data[].storeId").description("매장 ID"),
                                fieldWithPath("data[].storeName").description("매장 이름"),
                                fieldWithPath("data[].createdBy").description("작성자 이메일"),
                                fieldWithPath("data[].createdAt").description("캠페인 생성 일시 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data[].updatedAt").description("캠페인 수정 일시 (yyyy-MM-ddTHH:mm:ss)"),
                                fieldWithPath("data[].coupons").description("연결된 쿠폰 목록 ")
                        )
                ));
    }
}