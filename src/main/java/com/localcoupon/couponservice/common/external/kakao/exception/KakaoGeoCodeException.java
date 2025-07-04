package com.localcoupon.couponservice.common.external.kakao.exception;

import com.localcoupon.couponservice.common.exception.BaseException;
import com.localcoupon.couponservice.common.exception.ErrorCode;

public class KakaoGeoCodeException extends BaseException {
    public KakaoGeoCodeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
