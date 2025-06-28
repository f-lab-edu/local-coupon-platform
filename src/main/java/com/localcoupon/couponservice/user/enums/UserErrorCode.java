package com.localcoupon.couponservice.user.enums;

import com.localcoupon.couponservice.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("이미 존재하는 유저입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

    UserErrorCode(String message, HttpStatus status) {
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
