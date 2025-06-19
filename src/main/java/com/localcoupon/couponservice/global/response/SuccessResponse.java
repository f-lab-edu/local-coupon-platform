package com.localcoupon.couponservice.global.response;

public class SuccessResponse<T> extends BaseResponse {
    private final T data;

    private SuccessResponse(String message, T data) {
        super(true, message);
        this.data = data;
    }

    public static <T> SuccessResponse<T> of(String message, T data) {
        return new SuccessResponse<>(message, data);
    }

    public T getData() {
        return data;
    }
}
