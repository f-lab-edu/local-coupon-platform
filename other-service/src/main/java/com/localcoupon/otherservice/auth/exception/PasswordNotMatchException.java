package com.localcoupon.otherservice.auth.exception;

import com.localcoupon.otherservice.auth.enums.AuthErrorCode;
import com.localcoupon.otherservice.common.exception.BaseException;

public class PasswordNotMatchException extends BaseException {
    public PasswordNotMatchException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
