package com.localcoupon.otherservice.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCouponPeriod is a Querydsl query type for CouponPeriod
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCouponPeriod extends BeanPath<CouponPeriod> {

    private static final long serialVersionUID = -1128949293L;

    public static final QCouponPeriod couponPeriod = new QCouponPeriod("couponPeriod");

    public final DateTimePath<java.time.LocalDateTime> end = createDateTime("end", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> start = createDateTime("start", java.time.LocalDateTime.class);

    public QCouponPeriod(String variable) {
        super(CouponPeriod.class, forVariable(variable));
    }

    public QCouponPeriod(Path<? extends CouponPeriod> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCouponPeriod(PathMetadata metadata) {
        super(CouponPeriod.class, metadata);
    }

}

