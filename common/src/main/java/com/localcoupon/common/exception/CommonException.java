package com.localcoupon.common.exception;

import com.localcoupon.common.enums.CommonErrorCode;

public class CommonException extends BaseException {
    public CommonException(CommonErrorCode errorCode) {
        super(errorCode);
    }
}
