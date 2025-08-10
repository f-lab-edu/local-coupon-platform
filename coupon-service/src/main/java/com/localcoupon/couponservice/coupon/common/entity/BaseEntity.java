package com.localcoupon.couponservice.coupon.common.entity;

import com.localcoupon.common.constants.BaseColumns;
import com.localcoupon.common.enums.Result;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@SQLRestriction("is_deleted = false")
public abstract class BaseEntity {
    @CreatedDate
    @Column(name = BaseColumns.CREATED_AT, nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = BaseColumns.UPDATED_AT, nullable = false)
    protected LocalDateTime updatedAt;

    @Column(name = BaseColumns.IS_DELETED, nullable = false)
    protected boolean isDeleted = false;

    public Result delete() {
        this.isDeleted = true;
        return Result.SUCCESS;
    }

    @PreRemove
    public void softDelete() {
        this.isDeleted = true;
    }
}
