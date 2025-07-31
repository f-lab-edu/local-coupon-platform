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

    @Column(name = "qr_image_url")
    private String qrImageUrl;

    @Column(name = "is_used")
    private boolean isUsed;

    public IssuedCoupon(User user, Coupon coupon, LocalDateTime issuedAt, LocalDateTime usedAt, String qrToken, boolean isUsed, String qrImageUrl) {
        this.user = user;
        this.coupon = coupon;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
        this.qrToken = qrToken;
        this.qrImageUrl = qrImageUrl;
        this.isUsed = isUsed;
    }

    public IssuedCoupon(User user, Coupon coupon, LocalDateTime issuedAt) {
        this.user = user;
        this.coupon = coupon;
        this.issuedAt = issuedAt;
    }

    public IssuedCoupon use() {
        if (this.isUsed) {
            throw new UserCouponException(UserCouponErrorCode.ALREADY_COUPON_USED);
        }
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
        return this;
    }

    public IssuedCoupon postProcess (String qrToken, String qrImageUrl) {
        if (this.isUsed) {
            throw new UserCouponException(UserCouponErrorCode.ALREADY_COUPON_USED);
        }
        this.qrToken = qrToken;
        this.qrImageUrl = qrImageUrl;
        return this;
    }

    public static IssuedCoupon issueWithOutQrCode(User user, Coupon coupon, LocalDateTime issuedAt) {
        return IssuedCoupon.of(user,coupon,issuedAt);
    }

    public static IssuedCoupon of(User user, Coupon coupon, String qrToken,  String qrImageUrl, LocalDateTime issuedAt) {
        return new IssuedCoupon(user,coupon,issuedAt,null,qrToken,false, qrImageUrl);
    }

    public static IssuedCoupon of(User user, Coupon coupon, LocalDateTime issuedAt) {
        return new IssuedCoupon(user,coupon,issuedAt);
    }
}
