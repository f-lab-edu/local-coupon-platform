package com.localcoupon.couponservice.user.entity;

import com.localcoupon.couponservice.global.entity.BaseEntity;
import com.localcoupon.couponservice.global.util.PasswordEncoder;
import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
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

    public User(String email, String passwordEnc, String nickname, String address, String regionCode) {
        this.email = email;
        this.passwordEnc = passwordEnc;
        this.nickname = nickname;
        this.address = address;
        this.regionCode = regionCode;
    }

    public static User from(SignUpRequestDto dto) {
        return new User(
                dto.email(),
                PasswordEncoder.encrypt(dto.password()),
                dto.nickname(),
                dto.address(),
                dto.regionCode()
        );
    }
}
