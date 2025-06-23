package com.localcoupon.couponservice.user.entity;

import com.localcoupon.couponservice.global.entity.BaseEntity;
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

}
