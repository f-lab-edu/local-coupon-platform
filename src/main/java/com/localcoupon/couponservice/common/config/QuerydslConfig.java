package com.localcoupon.couponservice.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
JPAQueryFactory를 Spring Bean으로 등록하는 설정 클래스
 */
@Configuration
public class QuerydslConfig {
    //트랜잭션마다  각유저마다 em은 다르기 때문에 @PersistenceContext로 프록시 EM을 가져와 연결해준다.
    //변경 시에는 트랜잭션 필수 조회 시에는 EntityManager의 프록시 객체를 들고오므로 실제 db커넥션이나 영속성 컨텍스트가 없는 em이 주입된다.
    //JPA는 트랜잭션
    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
