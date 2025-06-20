package com.localcoupon.couponservice.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SuccessResponse<T> extends BaseResponse {
    private final T data;

    private SuccessResponse(String message, HttpStatus status, T data) {
        super(true, message, status);
        this.data = data;
    }

    public static <T> SuccessResponse<T> of(String message, T data) {
        return new SuccessResponse<>(message, HttpStatus.OK, data);
    }

    public static <T> SuccessResponse<T> of(String message, HttpStatus status, T data) {
        return new SuccessResponse<>(message, status, data);
    }
}
