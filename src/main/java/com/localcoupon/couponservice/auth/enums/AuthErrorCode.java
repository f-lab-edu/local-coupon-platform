package com.localcoupon.couponservice.auth.enums;

import com.localcoupon.couponservice.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements ErrorCode {
    PASSWORD_NOT_MATCHING("비밀번호가 틀렸습니다.", HttpStatus.BAD_REQUEST),
    SESSION_NOT_EXISTS("세션이 없습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;

    AuthErrorCode(String message, HttpStatus status) {
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
