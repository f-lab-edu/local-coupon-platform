package com.localcoupon.couponservice.common;

import com.localcoupon.couponservice.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {
    BAD_REQUEST("잘못된 요청 입니다.", HttpStatus.BAD_REQUEST),
    SERVER_ERROR("서버 에러 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("데이터베이스 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_SERIALIZE_ERROR("JSON 직렬화에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    private final String message;
    private final HttpStatus status;

    CommonErrorCode(String message, HttpStatus status) {
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
