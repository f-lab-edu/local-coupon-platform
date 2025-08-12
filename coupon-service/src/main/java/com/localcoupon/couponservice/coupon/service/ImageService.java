package com.localcoupon.couponservice.coupon.service;

import java.awt.image.BufferedImage;

public interface ImageService {
    String uploadQrImage(BufferedImage image, String filename);
}
