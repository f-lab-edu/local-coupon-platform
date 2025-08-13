package com.localcoupon.otherservice.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = -152398478L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final com.localcoupon.otherservice.common.entity.QBaseEntity _super = new com.localcoupon.otherservice.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final NumberPath<Integer> issuedCount = createNumber("issuedCount", Integer.class);

    public final QCouponPeriod issuePeriod;

    public final EnumPath<com.localcoupon.otherservice.coupon.enums.CouponScope> scope = createEnum("scope", com.localcoupon.otherservice.coupon.enums.CouponScope.class);

    public final com.localcoupon.otherservice.store.entity.QStore store;

    public final StringPath title = createString("title");

    public final NumberPath<Integer> totalCount = createNumber("totalCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QCouponPeriod validPeriod;

    public QCoupon(String variable) {
        this(Coupon.class, forVariable(variable), INITS);
    }

    public QCoupon(Path<? extends Coupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCoupon(PathMetadata metadata, PathInits inits) {
        this(Coupon.class, metadata, inits);
    }

    public QCoupon(Class<? extends Coupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.issuePeriod = inits.isInitialized("issuePeriod") ? new QCouponPeriod(forProperty("issuePeriod")) : null;
        this.store = inits.isInitialized("store") ? new com.localcoupon.otherservice.store.entity.QStore(forProperty("store")) : null;
        this.validPeriod = inits.isInitialized("validPeriod") ? new QCouponPeriod(forProperty("validPeriod")) : null;
    }

}

