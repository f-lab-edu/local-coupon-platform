package com.localcoupon.couponservice.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseResponse {
    protected final boolean success;
    protected final String message;
    protected final HttpStatus httpStatus;

    protected BaseResponse(boolean success, String message, HttpStatus httpStatus) {
        this.success = success;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
