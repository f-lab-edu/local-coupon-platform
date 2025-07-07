package com.localcoupon.couponservice.common.config;

import com.localcoupon.couponservice.common.util.StringUtils;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@Configuration
public class GlobalFilterConfig {
    @Bean
    public FilterRegistrationBean<Filter> contentTypeFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter((request, response, chain) -> {
            if (StringUtils.isEmpty(response.getContentType())) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            }
            chain.doFilter(request, response);
        });
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
