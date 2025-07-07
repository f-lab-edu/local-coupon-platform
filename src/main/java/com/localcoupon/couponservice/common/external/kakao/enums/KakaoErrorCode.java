package com.localcoupon.couponservice.common.external.kakao.enums;

import com.localcoupon.couponservice.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum KakaoErrorCode implements ErrorCode {
    DATA_SEARCH_FAILED("주소로 좌표를 찾을 수 없습니다", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

    KakaoErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
