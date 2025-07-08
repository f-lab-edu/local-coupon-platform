package com.localcoupon.couponservice.coupon.repository.impl;

import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.QCoupon;
import com.localcoupon.couponservice.coupon.repository.CouponRepositoryCustom;
import com.localcoupon.couponservice.store.entity.QStore;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepositoryCustom {

    // QueryDSL JPQL을 Q클래스를 통해 타입 세이프하게 빌드,
    // JpaQueryFacotry는 Bean으로 등록되어있다. QueryDslConfig
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Coupon> findAllByOwnerIdWithCursorPaging(Long ownerId, CursorPageRequest request) {
        //QueryDSL이 생성한 클래스들 정의
        QCoupon coupon = QCoupon.coupon;
        QStore store = QStore.store;


        //엔티티 이름 정보 불러오기 path.get("id") -> coupon.id
        PathBuilder<Coupon> path = new PathBuilder<>(Coupon.class, coupon.getMetadata().getName());

        //기본 정렬 세팅
        OrderSpecifier<?> orderSpecifier = new OrderSpecifier(
                request.direction() == SortDirection.ASCENDING
                        ? Order.ASC
                        : Order.DESC,
                path.get(request.sortBy())
        );

        return queryFactory
                .selectFrom(coupon)
                .join(coupon.store, store).fetchJoin()
                .where(
                        coupon.isDeleted.isFalse(),
                        store.ownerId.eq(ownerId),
                        cursorPaging(coupon, request)
                )
                .orderBy(orderSpecifier)
                .limit(request.size())
                .fetch();
    }

    //QueryDSL은 WHERE절 조건을 Predicate BooleanExpression으로 받는다.
    private BooleanExpression cursorPaging(QCoupon coupon, CursorPageRequest request) {
        return Optional.ofNullable(request.cursor())
                .map(cursor -> {
                    if (request.direction() == SortDirection.ASCENDING) {
                        return coupon.id.gt(cursor);
                    } else {
                        return coupon.id.lt(cursor);
                    }
                }).orElse(null);
    }

}
