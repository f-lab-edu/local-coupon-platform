package com.localcoupon.couponservice.user.entity;

import com.localcoupon.couponservice.common.entity.BaseEntity;
import com.localcoupon.couponservice.common.util.PasswordEncoder;
import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_enc", nullable = false)
    private String passwordEnc;

    @Column(nullable = false)
    private String nickname;

    private String address;

    @Column(name = "region_code")
    private String regionCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public User(String email, String passwordEnc, String nickname, String address, String regionCode, UserRole role) {
        this.email = email;
        this.passwordEnc = passwordEnc;
        this.nickname = nickname;
        this.address = address;
        this.regionCode = regionCode;
        this.role = role;
    }

    public static User from(SignUpRequestDto dto) {
        return new User(
                dto.email(),
                PasswordEncoder.encrypt(dto.password()),
                dto.nickname(),
                dto.address(),
                dto.regionCode(),
                UserRole.ROLE_USER // 기본 가입자는 USER
        );
    }
}
