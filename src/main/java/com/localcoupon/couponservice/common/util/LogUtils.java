package com.localcoupon.couponservice.common.util;

import com.localcoupon.couponservice.common.dto.LogContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {
    // 예외 포함된 버전 (LogContext 안에 예외 포함)
    public static void error(LogContext logContext) {
        log.error(
                "사용자: {}, IP: {}, 요청: {} {}, 동작: {}, 예외: ",
                logContext.userId(),
                logContext.ip(),
                logContext.method(),
                logContext.uri(),
                logContext.action(),
                logContext.exception()
        );
    }
}
