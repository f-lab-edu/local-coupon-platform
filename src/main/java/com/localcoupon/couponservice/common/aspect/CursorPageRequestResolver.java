package com.localcoupon.couponservice.common.aspect;

import com.localcoupon.couponservice.common.annotation.CursorRequest;
import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Component
public class CursorPageRequestResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CursorRequest.class)
                && parameter.getParameterType().equals(CursorPageRequest.class);

    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Long cursor = webRequest.getParameter("cursor") != null ? Long.valueOf(Objects.requireNonNull(webRequest.getParameter("cursor"))) : null;
        Integer size = webRequest.getParameter("size") != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter("size"))) : null;
        String sortBy = webRequest.getParameter("sortBy");
        String direction = webRequest.getParameter("direction");

        return CursorPageRequest.of(cursor, size, sortBy, direction);
    }
}
