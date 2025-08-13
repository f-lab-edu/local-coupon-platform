package com.localcoupon.couponservice.coupon.dto;

import org.hibernate.query.SortDirection;
import java.util.Set;

public record CursorPageRequest(
        Long cursor,
        int size,
        String sortBy,
        SortDirection direction
) {
    public static final int DEFAULT_SIZE = 20;
    public static final String DEFAULT_SORT_BY = "id";
    private static final Set<String> ALLOWED_SORTS = Set.of("id", "title", "createdAt", "updatedAt");

    public static CursorPageRequest of(
            Long cursor,
            Integer size,
            String sortBy,
            String direction
    ) {
        String sort = (sortBy == null || sortBy.isBlank()) ? DEFAULT_SORT_BY : sortBy.trim();

        if (!ALLOWED_SORTS.contains(sort)) sort = DEFAULT_SORT_BY;

        SortDirection dir = parseDirection(direction);

        return new CursorPageRequest(
                cursor == null ? 0L : cursor,
                size == null ? DEFAULT_SIZE : size,
                sort,
                dir
        );
    }

    private static SortDirection parseDirection(String userDirection) {
        if (userDirection == null || userDirection.isBlank()) return SortDirection.DESCENDING;
        String direction = userDirection.trim().toUpperCase();
        return switch (direction) {
            case "ASC", "ASCENDING" -> SortDirection.ASCENDING;
            default -> SortDirection.DESCENDING;
        };
    }
}
