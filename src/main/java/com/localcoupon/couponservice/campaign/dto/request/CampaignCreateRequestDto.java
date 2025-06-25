package com.localcoupon.couponservice.campaign.dto.request;

import java.time.LocalDate;
import java.util.List;

public record CampaignCreateRequestDto(
        String title,
        String description,
        LocalDate campaignStartTime,
        LocalDate campaignEndTime,
        List<Long> storeIds
) {}
