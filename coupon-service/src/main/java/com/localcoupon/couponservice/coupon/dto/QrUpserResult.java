package com.localcoupon.couponservice.coupon.dto;

public record QrUpserResult(Long id, String qrImageUrl) {

  public static QrUpserResult of(Long id, String qrImageUrl) {
    return new QrUpserResult(id, qrImageUrl);
  }
}
