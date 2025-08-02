package com.localcoupon.couponservice.coupon.entity;

import com.localcoupon.couponservice.common.entity.BaseEntity;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.enums.CouponStock;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.store.entity.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Table(name = "coupon")
@Builder
@AllArgsConstructor
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;


    @Enumerated(EnumType.STRING)
    private CouponScope scope;

    @Column
    @Length(max = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "coupon_total_count")
    private int totalCount;

    @Column(name = "coupon_issued_count")
    private int issuedCount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "coupon_valid_start_time")),
            @AttributeOverride(name = "end", column = @Column(name = "coupon_valid_end_time"))
    })
    private CouponPeriod validPeriod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "coupon_issue_start_time")),
            @AttributeOverride(name = "end", column = @Column(name = "coupon_issue_end_time"))
    })
    private CouponPeriod issuePeriod;

    public Coupon() {
    }

    public Coupon(
            CouponScope scope,
            String title,
            String description,
            Integer totalCount,
            Integer issuedCount,
            CouponPeriod validPeriod,
            CouponPeriod issuePeriod,
            Store store
    ) {
        super();
        this.store = store;
        this.scope = scope;
        this.title = title;
        this.description = description;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.validPeriod = validPeriod;
        this.issuePeriod = issuePeriod;
    }


    public Coupon syncIssuedCount(int redisIssuedCount) {
        if(redisIssuedCount < 0) {
            throw new UserCouponException(UserCouponErrorCode.SOLD_OUT_COUPON);
        }
        this.issuedCount = redisIssuedCount;
        return this;
    }

    public static Coupon from(CouponCreateRequestDto request, Store store) {
        return new Coupon(
                request.scope(),
                request.title(),
                request.description(),
                request.totalCount(),
                CouponStock.INIT.getValue(),
                request.validPeriod(),
                request.issuePeriod(),
                store
        );
    }

    public Coupon update(CouponUpdateRequestDto request) {
        if (request.scope() != null) {
            this.scope = request.scope();
        }
        if (request.title() != null) {
            this.title = request.title();
        }
        if (request.description() != null) {
            this.description = request.description();
        }
        if (request.totalCount() != null) {
            this.totalCount = request.totalCount();
        }
        if (request.validPeriod() != null && request.validPeriod().getStart() != null && request.validPeriod().getEnd() != null) {
            this.validPeriod = new CouponPeriod(request.validPeriod().getStart(), request.validPeriod().getEnd());
        }
        if (request.issuePeriod() != null && request.issuePeriod().getStart() != null && request.issuePeriod().getEnd() != null) {
            this.issuePeriod = new CouponPeriod(request.issuePeriod().getStart(), request.issuePeriod().getEnd());
        }
        return this;
    }
}

