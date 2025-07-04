package com.localcoupon.couponservice.auth.security;

import com.localcoupon.couponservice.auth.dto.UserSessionDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String email;
    private final String nickname;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String email, String nickname, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.authorities = authorities;
    }

    public static CustomUserDetails from(UserSessionDto userSessionDto) {
        return new CustomUserDetails(userSessionDto.id(), userSessionDto.email(), userSessionDto.nickname(),
                userSessionDto.roles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // 로그인 이후엔 필요 없는 값
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
