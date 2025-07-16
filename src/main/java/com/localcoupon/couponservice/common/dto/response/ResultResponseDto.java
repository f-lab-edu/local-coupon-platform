package com.localcoupon.couponservice.common.dto.response;

import com.localcoupon.couponservice.common.enums.Result;

public record ResultResponseDto(int code) {
    public static ResultResponseDto from(Result result) {
        return new ResultResponseDto(result.getValue());
    }
}

