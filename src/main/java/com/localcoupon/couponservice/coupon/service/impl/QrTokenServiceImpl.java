package com.localcoupon.couponservice.coupon.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.localcoupon.couponservice.common.enums.CommonErrorCode;
import com.localcoupon.couponservice.common.exception.CommonException;
import com.localcoupon.couponservice.common.util.TokenGenerator;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import com.localcoupon.couponservice.infra.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class QrTokenServiceImpl implements QrTokenService {
    private final ImageService cloudinaryService;

    @Override
    public String generateQrToken(Long issuedCouponId, LocalDateTime validStartTime, LocalDateTime validEndTime) {
        //쿠폰은 랜덤값과 발급쿠폰ID, 유효기간이 명시되어 있다.
        return Base64.getEncoder().encodeToString(
                String.join(":",
                                TokenGenerator.createSessionToken(),
                                issuedCouponId.toString(),
                                validStartTime.toString(),
                                validEndTime.toString())
                        .getBytes(StandardCharsets.UTF_8));
    }

    //createQrImage
    @Override
    public String uploadQrImage(String qrToken) {
        try {
            BufferedImage qrImage = generateQrImage(qrToken);
            return cloudinaryService.uploadQrImage(qrImage, "issued-coupon/" + qrToken);
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.QR_CREATE_OPERATION_ERROR);
        }
    }

    public boolean isTokenValid(String token) {
        return Stream.of(token)
                .map(data -> new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8))
                .filter(data -> data.split(":").length == 4)
                .anyMatch(data -> {
                    String[] parts = data.split(":");
                    LocalDateTime start = LocalDateTime.parse(parts[2]);
                    LocalDateTime end = LocalDateTime.parse(parts[3]);
                    LocalDateTime now = LocalDateTime.now();
                    return !now.isBefore(start) && !now.isAfter(end);  // start <= now <= end
                });
    }

    private BufferedImage generateQrImage(String content) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 250, 250);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
