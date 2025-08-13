package com.localcoupon.couponservice.coupon.common.resolver;

import com.localcoupon.couponservice.coupon.annotation.CursorRequest;
import com.localcoupon.couponservice.coupon.dto.CursorPageRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CursorPageRequestArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean match = parameter.hasParameterAnnotation(CursorRequest.class)
                && CursorPageRequest.class.isAssignableFrom(parameter.getParameterType());
        System.out.println("[DEBUG] supportsParameter: " + match + " for " + parameter.getParameterName());
        return match;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        String cursor = webRequest.getParameter("cursor");
        String size = webRequest.getParameter("size");
        String sortBy = webRequest.getParameter("sortBy");
        String direction = webRequest.getParameter("direction");

        Long cursorVal = (cursor != null && !cursor.isBlank()) ? Long.valueOf(cursor) : null;
        Integer sizeVal = (size != null && !size.isBlank()) ? Integer.valueOf(size) : null;

        System.out.println("[DEBUG] resolveArgument values: " + cursorVal + ", " + sizeVal + ", " + sortBy + ", " + direction);

        return CursorPageRequest.of(cursorVal, sizeVal, sortBy, direction);
    }
}
