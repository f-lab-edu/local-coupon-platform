package com.localcoupon.couponservice.global.entity;

import com.localcoupon.couponservice.global.constants.BaseColumns;
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
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = BaseColumns.UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = BaseColumns.IS_DELETED, nullable = false)
    private boolean isDeleted = false;
}
