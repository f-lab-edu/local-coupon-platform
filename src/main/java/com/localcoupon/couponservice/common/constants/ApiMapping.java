package com.localcoupon.couponservice.common.constants;

public final class ApiMapping {
    public static final String USER = API_Prefix.API_V1 + "/users";
    public static final String AUTH = API_Prefix.API_V1 + "/auth";
    public static final String COUPON_MANAGE_BASE = API_Prefix.API_V1 + "/coupon-manage";
    public static final String STORE = API_Prefix.API_V1 + "/stores";
    public static final String CAMPAIGN = API_Prefix.API_V1 + "/campaigns";
    public static final String USER_COUPON_BASE = API_Prefix.API_V1 + "/user-coupons";
   public static final class API_Prefix {
       public static final String API_V1 = "/api/v1";
   }
}
