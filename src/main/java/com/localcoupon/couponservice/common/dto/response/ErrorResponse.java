package com.localcoupon.couponservice.common.dto.response;

import com.localcoupon.couponservice.common.exception.ErrorCode;
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
