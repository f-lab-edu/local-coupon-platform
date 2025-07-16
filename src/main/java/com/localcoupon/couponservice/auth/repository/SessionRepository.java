package com.localcoupon.couponservice.auth.repository;

import com.localcoupon.couponservice.auth.dto.UserSessionDto;

public interface SessionRepository {
    boolean save(String sessionToken, UserSessionDto sessionDto);
    boolean delete(String sessionToken);
    UserSessionDto get(String sessionToken);
}
