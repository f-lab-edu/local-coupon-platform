package com.localcoupon.couponservice.common.util;


import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public final class TimeProvider {
    private final Clock clock;

    public TimeProvider(Clock clock) {
        this.clock = clock;
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
    public LocalDate today() {
        return LocalDate.now(clock);
    }
}

