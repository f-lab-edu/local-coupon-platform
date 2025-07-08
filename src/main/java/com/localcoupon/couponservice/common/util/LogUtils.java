package com.localcoupon.couponservice.common.util;

import com.localcoupon.couponservice.common.dto.LogContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {
    public static void error(LogContext logContext, Throwable e) {
            log.error(
                    "사용자: {}, IP: {}, 요청: {} {}, 동작: {}",
                    logContext.userId(),
                    logContext.ip(),
                    logContext.method(),
                    logContext.uri(),
                    logContext.action(),
                    e
            );
    }
    public static void error(LogContext logContext) {
        log.error(
                "사용자: {}, IP: {}, 요청: {} {}, 동작: {}",
                logContext.userId(),
                logContext.ip(),
                logContext.method(),
                logContext.uri(),
                logContext.action()
        );
    }
}
