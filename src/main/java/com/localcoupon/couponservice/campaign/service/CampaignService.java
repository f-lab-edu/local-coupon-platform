package com.localcoupon.couponservice.campaign.service;

import com.localcoupon.couponservice.campaign.dto.request.CampaignCreateRequestDto;
import com.localcoupon.couponservice.campaign.dto.response.CampaignResponseDto;

import java.util.List;

public interface CampaignService {

    CampaignResponseDto createCampaign(CampaignCreateRequestDto request);

    List<CampaignResponseDto> getCampaigns();
}
