package com.localcoupon.couponservice.global.response;

public abstract class BaseResponse {
    protected final boolean success;
    protected final String message;

    protected BaseResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
