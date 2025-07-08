package com.localcoupon.couponservice.common.dto.response;

import com.localcoupon.couponservice.common.enums.ResultCode;

public record ResultCodeResponseDto(int code) {
    public static ResultCodeResponseDto from(ResultCode resultCode) {
        return new ResultCodeResponseDto(resultCode.getValue());
    }
}

