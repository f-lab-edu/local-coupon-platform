package com.localcoupon.otherservice.store.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStore is a Querydsl query type for Store
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStore extends EntityPathBase<Store> {

    private static final long serialVersionUID = 1402594732L;

    public static final QStore store = new QStore("store");

    public final com.localcoupon.otherservice.common.entity.QBaseEntity _super = new com.localcoupon.otherservice.common.entity.QBaseEntity(this);

    public final StringPath address = createString("address");

    public final EnumPath<com.localcoupon.otherservice.store.enums.StoreCategory> category = createEnum("category", com.localcoupon.otherservice.store.enums.StoreCategory.class);

    public final ListPath<com.localcoupon.otherservice.coupon.entity.Coupon, com.localcoupon.otherservice.coupon.entity.QCoupon> coupons = this.<com.localcoupon.otherservice.coupon.entity.Coupon, com.localcoupon.otherservice.coupon.entity.QCoupon>createList("coupons", com.localcoupon.otherservice.coupon.entity.Coupon.class, com.localcoupon.otherservice.coupon.entity.QCoupon.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> ownerId = createNumber("ownerId", Long.class);

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath regionCode = createString("regionCode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QStore(String variable) {
        super(Store.class, forVariable(variable));
    }

    public QStore(Path<? extends Store> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStore(PathMetadata metadata) {
        super(Store.class, metadata);
    }

}

