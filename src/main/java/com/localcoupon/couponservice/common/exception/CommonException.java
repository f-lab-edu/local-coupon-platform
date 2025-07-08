package com.localcoupon.couponservice.common.exception;

import com.localcoupon.couponservice.common.enums.CommonErrorCode;

public class CommonException extends BaseException {
    public CommonException(CommonErrorCode errorCode) {
        super(errorCode);
    }
}
