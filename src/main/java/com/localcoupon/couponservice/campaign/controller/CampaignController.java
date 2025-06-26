package com.localcoupon.couponservice.campaign.controller;

import com.localcoupon.couponservice.campaign.dto.request.CampaignCreateRequestDto;
import com.localcoupon.couponservice.campaign.dto.response.CampaignResponseDto;
import com.localcoupon.couponservice.campaign.service.CampaignService;
import com.localcoupon.couponservice.global.constants.ApiMapping;
import com.localcoupon.couponservice.global.dto.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.API_Prefix.API_V1 + ApiMapping.CAMPAIGN)
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping
    public SuccessResponse<List<CampaignResponseDto>> getCampaigns() {
        return SuccessResponse.of(campaignService.getCampaigns());
    }

    // 캠페인 등록 (점주 또는 관리자)
    @PostMapping
    public SuccessResponse<CampaignResponseDto> createCampaign(
            @RequestBody CampaignCreateRequestDto request
    ) {
        return SuccessResponse.of(campaignService.createCampaign(request));
    }

}
