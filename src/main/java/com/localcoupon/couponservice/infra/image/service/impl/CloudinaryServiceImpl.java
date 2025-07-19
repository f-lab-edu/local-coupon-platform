package com.localcoupon.couponservice.infra.image.service.impl;

import com.cloudinary.Cloudinary;
import com.localcoupon.couponservice.common.enums.CommonErrorCode;
import com.localcoupon.couponservice.common.enums.ImageFormat;
import com.localcoupon.couponservice.common.exception.CommonException;
import com.localcoupon.couponservice.infra.dto.CloudinaryUploadOption;
import com.localcoupon.couponservice.infra.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements ImageService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadQrImage(BufferedImage image, String filename) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            //이미지 바이트 배열 만들기
            ImageIO.write(image, ImageFormat.PNG.getFormatName(), os);
            byte[] bytes = os.toByteArray();

            //Cloudinary로 이미지 전송
            Map uploadResult = cloudinary.uploader().upload(
                    bytes,
                    CloudinaryUploadOption.of(filename).toMap()
            );

            //Uri 리턴
            return cloudinary.url()
                    .signed(true)
                    .resourceType("image")
                    .type("authenticated")
                    .version(uploadResult.get("version").toString())
                    .generate(uploadResult.get("public_id").toString());

        } catch (IOException e) {
            log.error("[CloudinaryServiceImpl] QR Token Image Upload Error", e);
            throw new CommonException(CommonErrorCode.CLOUDINARY_OPERATION_ERROR);
        }
    }

}
