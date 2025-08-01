package com.localcoupon.couponservice.common.dto.request;

import com.localcoupon.couponservice.common.util.StringUtils;
import org.hibernate.query.SortDirection;

public record CursorPageRequest(
        Long cursor,
        int size,
        String sortBy,
        SortDirection direction
) {
    public static final int DEFAULT_SIZE = 20;
    public static final String DEFAULT_SORT_BY = "id";

    public static CursorPageRequest of(
            Long cursor,
            Integer size,
            String sortBy,
            String direction
    ) {
        return new CursorPageRequest(
                cursor == null ? 0L : cursor,
                size == null ? DEFAULT_SIZE : size,
                StringUtils.isEmpty(sortBy) ? DEFAULT_SORT_BY : sortBy,
                StringUtils.isEmpty(direction) ? SortDirection.DESCENDING : SortDirection.valueOf(direction.toUpperCase())
        );
    }
}

