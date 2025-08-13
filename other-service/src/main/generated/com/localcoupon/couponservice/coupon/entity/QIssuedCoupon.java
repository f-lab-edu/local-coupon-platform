package com.localcoupon.otherservice.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIssuedCoupon is a Querydsl query type for IssuedCoupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIssuedCoupon extends EntityPathBase<IssuedCoupon> {

    private static final long serialVersionUID = 97181437L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIssuedCoupon issuedCoupon = new QIssuedCoupon("issuedCoupon");

    public final QCoupon coupon;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> issuedAt = createDateTime("issuedAt", java.time.LocalDateTime.class);

    public final BooleanPath isUsed = createBoolean("isUsed");

    public final StringPath qrImageUrl = createString("qrImageUrl");

    public final StringPath qrToken = createString("qrToken");

    public final DateTimePath<java.time.LocalDateTime> usedAt = createDateTime("usedAt", java.time.LocalDateTime.class);

    public final com.localcoupon.otherservice.user.entity.QUser user;

    public QIssuedCoupon(String variable) {
        this(IssuedCoupon.class, forVariable(variable), INITS);
    }

    public QIssuedCoupon(Path<? extends IssuedCoupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIssuedCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIssuedCoupon(PathMetadata metadata, PathInits inits) {
        this(IssuedCoupon.class, metadata, inits);
    }

    public QIssuedCoupon(Class<? extends IssuedCoupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon"), inits.get("coupon")) : null;
        this.user = inits.isInitialized("user") ? new com.localcoupon.otherservice.user.entity.QUser(forProperty("user")) : null;
    }

}

