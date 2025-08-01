package com.localcoupon.couponservice.common.entity;

import com.localcoupon.couponservice.common.constants.BaseColumns;
import com.localcoupon.couponservice.common.enums.Result;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
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
}
