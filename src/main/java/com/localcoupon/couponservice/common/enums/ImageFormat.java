package com.localcoupon.couponservice.common.enums;

public enum ImageFormat {
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif"),
    BMP("bmp"),
    TIFF("tiff");

    private final String formatName;

    ImageFormat(String formatName) {
        this.formatName = formatName;
    }

    public String getFormatName() {
        return formatName;
    }
}
