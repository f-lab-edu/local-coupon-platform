package com.localcoupon.couponservice.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {

        HikariConfig config = new HikariConfig();

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(40);
        config.setMinimumIdle(10);
        config.setIdleTimeout(300_000); // 커넥션이 5분동안 놀을 떄 (정리)
        config.setMaxLifetime(180_000_0); // 커넥션 교체 시간 (항상 교체 작업) 30분)
        config.setConnectionTimeout(5000); //5초 대기

        config.setPoolName("LocalCouponHikariCP");
        config.setAutoCommit(false); //Spring Transaction 사용하도록 변경
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");

        return new HikariDataSource(config);
    }
}
