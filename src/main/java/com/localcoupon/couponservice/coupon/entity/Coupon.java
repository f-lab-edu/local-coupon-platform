package com.localcoupon.couponservice.coupon.entity;

import com.localcoupon.couponservice.common.entity.BaseEntity;
import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.enums.CouponStock;
import com.localcoupon.couponservice.store.entity.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

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

//    @ManyToOne(fetch = FetchType.LAZY) //TODO Campaign 작업 시 추가
//    @JoinColumn(name = "campaign_id")
//    private Campaign campaign;

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

    @Column(name = "coupon_valid_start_time")
    private LocalDateTime couponValidStartTime;

    @Column(name = "coupon_valid_end_time")
    private LocalDateTime couponValidEndTime;

    @Column(name = "coupon_issue_start_time")
    private LocalDateTime couponIssueStartTime;

    @Column(name = "coupon_issue_end_time")
    private LocalDateTime couponIssueEndTime;

    public Coupon() {
    }

    public Coupon(
            CouponScope scope,
            String title,
            String description,
            Integer totalCount,
            Integer issuedCount,
            LocalDateTime couponValidStartTime,
            LocalDateTime couponValidEndTime,
            LocalDateTime couponIssueStartTime,
            LocalDateTime couponIssueEndTime,
            Store store
    ) {
        super();
        this.store = store;
        this.scope = scope;
        this.title = title;
        this.description = description;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.couponValidStartTime = couponValidStartTime;
        this.couponValidEndTime = couponValidEndTime;
        this.couponIssueStartTime = couponIssueStartTime;
        this.couponIssueEndTime = couponIssueEndTime;
    }

    public boolean isExpiredForUse(LocalDateTime now) {
        return now.isAfter(couponValidEndTime);
    }

    public boolean isExpiredForIssue(LocalDateTime now) {
        return now.isAfter(couponIssueEndTime);
    }

    public Coupon syncIssuedCount(int redisIssuedCount) {
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
                request.couponValidStartTime(),
                request.couponValidEndTime(),
                request.couponIssueStartTime(),
                request.couponIssueEndTime(),
                store
        );
    }

    public Result update(CouponUpdateRequestDto request) {
        this.scope = request.scope();
        this.title = request.title();
        this.description = request.description();
        this.totalCount = request.totalCount();
        this.couponValidStartTime = request.couponValidStartTime();
        this.couponValidEndTime = request.couponValidEndTime();
        this.couponIssueStartTime = request.couponIssueStartTime();
        this.couponIssueEndTime = request.couponIssueEndTime();
        return Result.SUCCESS;
    }
}

