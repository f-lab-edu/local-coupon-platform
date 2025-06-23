package com.localcoupon.couponservice.auth.exception;

import com.localcoupon.couponservice.auth.enums.AuthErrorCode;
import com.localcoupon.couponservice.global.exception.BaseException;
import com.localcoupon.couponservice.global.exception.ErrorCode;

public class PasswordNotMatchException extends BaseException {
    public PasswordNotMatchException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
