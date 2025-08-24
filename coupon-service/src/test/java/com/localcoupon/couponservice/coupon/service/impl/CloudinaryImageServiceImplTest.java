package com.localcoupon.couponservice.coupon.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.Url;
import com.localcoupon.common.enums.ImageFormat;
import com.localcoupon.couponservice.coupon.common.exception.CommonException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CloudinaryImageServiceImplTest {

  @Mock
  private Cloudinary cloudinary;

  @Mock
  private Uploader uploader;

  @Mock
  private Url url;

  @InjectMocks
  private CloudinaryImageServiceImpl imageService;

  @BeforeEach
  void setUp() {
    when(cloudinary.uploader()).thenReturn(uploader);
  }

  private BufferedImage sampleImage() throws IOException {
    BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    g.fillRect(0, 0, 20, 20);
    g.dispose();
    // sanity: 인코딩 가능 여부 확인(문제시 테스트 자체가 실패)
    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      ImageIO.write(img, ImageFormat.PNG.getFormatName(), os);
    }
    return img;
  }

  @Test
  @DisplayName("Cloudinary 업로드 성공 시 서명된 URL 반환")
  void uploadQrImage_success() throws Exception {
    // given
    BufferedImage image = sampleImage();
    String filename = "test-file";
    Map<String, Object> uploadResult = Map.of(
        "version", "123",
        "public_id", "public/abc"
    );

    when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

    // Url builder 체이닝 스텁
    when(url.signed(true)).thenReturn(url);
    when(url.resourceType("image")).thenReturn(url);
    when(url.type("authenticated")).thenReturn(url);
    when(url.version("123")).thenReturn(url);
    when(url.generate("public/abc"))
        .thenReturn("https://res.cloudinary.com/demo/image/authenticated/v123/public/abc");

    // when
    String resultUrl = imageService.uploadQrImage(image, filename);

    // then
    assertThat(resultUrl).isEqualTo(
        "https://res.cloudinary.com/demo/image/authenticated/v123/public/abc");
    verify(cloudinary, times(1)).uploader();
    verify(uploader, times(1)).upload(any(byte[].class), anyMap());
    verify(cloudinary, atLeastOnce()).url();
    verify(url).signed(true);
    verify(url).resourceType("image");
    verify(url).type("authenticated");
    verify(url).version("123");
    verify(url).generate("public/abc");
  }

  @Test
  @DisplayName("Cloudinary 업로드 IOException 시 CommonException(CLOUDINARY_OPERATION_ERROR) 발생")
  void uploadQrImage_ioError() throws Exception {
    // given
    BufferedImage image = sampleImage();
    when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("io error"));

    // when
    assertThatThrownBy(() -> imageService.uploadQrImage(image, "any"))
        .isInstanceOf(CommonException.class);

    //then
    verify(uploader, times(1)).upload(any(byte[].class), anyMap());
    verify(url, never()).generate(anyString());
  }
}
