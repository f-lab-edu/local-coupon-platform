package com.localcoupon.couponservice.store.exception;

import com.localcoupon.couponservice.common.exception.BaseException;
import com.localcoupon.couponservice.common.exception.ErrorCode;

public class StoreException extends BaseException {
    public StoreException(ErrorCode errorCode) {
        super(errorCode);
    }
}
