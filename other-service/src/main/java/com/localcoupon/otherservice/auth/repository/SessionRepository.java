package com.localcoupon.otherservice.auth.repository;

import com.localcoupon.otherservice.auth.dto.UserSessionDto;

public interface SessionRepository {
    boolean save(String sessionToken, UserSessionDto sessionDto);
    boolean delete(String sessionToken);
    UserSessionDto get(String sessionToken);
}
