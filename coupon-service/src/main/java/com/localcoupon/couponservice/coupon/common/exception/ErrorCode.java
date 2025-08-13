package com.localcoupon.couponservice.coupon.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getCode();
    String getMessage();
    HttpStatus getStatus();
}
