package com.localcoupon.couponservice.infra.image.service;

import java.awt.image.BufferedImage;

public interface ImageService {
    String uploadQrImage(BufferedImage image, String filename);
}
