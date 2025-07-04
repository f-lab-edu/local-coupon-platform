package com.localcoupon.couponservice.auth.exception;

import com.localcoupon.couponservice.auth.enums.AuthErrorCode;
import com.localcoupon.couponservice.common.exception.BaseException;

public class PasswordNotMatchException extends BaseException {
    public PasswordNotMatchException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
