package com.localcoupon.common.dto.response;

import com.localcoupon.common.enums.Result;

public record ResultResponseDto(int code) {
    public static ResultResponseDto from(Result result) {
        return new ResultResponseDto(result.getValue());
    }
}

