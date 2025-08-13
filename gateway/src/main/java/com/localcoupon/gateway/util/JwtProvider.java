package com.localcoupon.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    private final String secretKey = "your-secret-key"; // 실제로는 환경변수로 관리

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserId(Claims claims) {
        return claims.getSubject(); // 일반적으로 userId를 subject에 저장
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }
}
