package com.localcoupon.couponservice.campaign.dto.response;

import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CampaignResponseDto(
        Long id,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Long storeId,
        String storeName,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CouponResponseDto> coupons
) {}

