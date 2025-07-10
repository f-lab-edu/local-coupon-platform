package com.localcoupon.couponservice.store.enums;

import com.localcoupon.couponservice.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum StoreErrorCode implements ErrorCode {
    STORE_NOT_FOUND_EXCEPTION("스토어 엔티티를 찾을 수 없습니다.", HttpStatus.NO_CONTENT);

    private final String message;
    private final HttpStatus status;
    StoreErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.status = httpStatus;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
