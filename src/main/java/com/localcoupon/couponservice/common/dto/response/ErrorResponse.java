package com.localcoupon.couponservice.common.dto.response;

import com.localcoupon.couponservice.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse extends BaseResponse {
    private final String errorCode;
    private final String detailMessage;

    private ErrorResponse(ErrorCode errorCode, String detailMessage) {
        super(false, errorCode.getMessage(), errorCode.getStatus());
        this.errorCode = errorCode.getCode();
        this.detailMessage = detailMessage;
    }

    private ErrorResponse(ErrorCode errorCode) {
        super(false, errorCode.getMessage(), errorCode.getStatus());
        this.errorCode = errorCode.getCode();
        this.detailMessage = null;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }
    public static ErrorResponse of(ErrorCode errorCode, String detailMessage) {
        return new ErrorResponse(errorCode, detailMessage);
    }
}
