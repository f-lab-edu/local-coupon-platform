package com.localcoupon.otherservice.auth.exception;

import com.localcoupon.common.exception.BaseException;
import com.localcoupon.otherservice.auth.enums.AuthErrorCode;

public class PasswordNotMatchException extends BaseException {
    public PasswordNotMatchException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
