package com.localcoupon.couponservice.coupon.entity;

import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "issued_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class IssuedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 해당 쿠폰
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "qr_token")
    private String qrToken;

    @Column(name = "is_used")
    private boolean isUsed;

    public IssuedCoupon use() {
        if (this.isUsed) {
            throw new UserCouponException(UserCouponErrorCode.ALREADY_COUPON_USED);
        }
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
        return this;
    }

    public void createQrToken(String token) {
        this.qrToken = token;
    }

}
