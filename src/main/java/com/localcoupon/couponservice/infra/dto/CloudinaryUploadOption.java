package com.localcoupon.couponservice.infra.dto;

import java.util.Map;
import java.util.HashMap;

public class CloudinaryUploadOption {

    private final String resourceType;
    private final String publicId;
    private final boolean overwrite;
    private final String type;

    // 생성자
    private CloudinaryUploadOption(String filename) {
        this.resourceType = "image";
        this.publicId = "qrcodes/" + filename;
        this.overwrite = true;
        this.type = "authenticated";
    }

    // Factory method
    public static CloudinaryUploadOption of(String filename) {
        return new CloudinaryUploadOption(filename);
    }

    // Convert to Map
    public Map<String, Object> toMap() {
        Map<String, Object> options = new HashMap<>();
        options.put("resource_type", resourceType);
        options.put("public_id", publicId);
        options.put("overwrite", overwrite);
        options.put("type", type);
        return options;
    }
}
