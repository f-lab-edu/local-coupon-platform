package com.localcoupon.couponservice.campaign.service.impl;

import com.localcoupon.couponservice.campaign.dto.request.CampaignCreateRequestDto;
import com.localcoupon.couponservice.campaign.dto.response.CampaignResponseDto;
import com.localcoupon.couponservice.campaign.service.CampaignService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Override
    public CampaignResponseDto createCampaign(CampaignCreateRequestDto request) {
        return null;
    }

    @Override
    public List<CampaignResponseDto> getCampaigns() {
        return List.of();
    }
}
