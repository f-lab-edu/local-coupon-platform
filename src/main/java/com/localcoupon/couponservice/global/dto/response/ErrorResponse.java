package com.localcoupon.couponservice.global.dto.response;

import com.localcoupon.couponservice.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse extends BaseResponse {
    private final String errorCode;

    private ErrorResponse(ErrorCode errorCode) {
        super(false, errorCode.getMessage(), errorCode.getStatus());
        this.errorCode = errorCode.getCode();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }
}
