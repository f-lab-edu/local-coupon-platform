package com.localcoupon.couponservice.global.exception;


import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }

    //Checked Exception을 감쌀때 사용
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getErrorCode();


    public abstract HttpStatus getHttpStatus();
}

