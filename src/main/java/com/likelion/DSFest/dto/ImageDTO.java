package com.likelion.DSFest.dto;

import com.likelion.DSFest.entity.Category;
import com.likelion.DSFest.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

public class ImageDTO {

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class responseImageDTO {
        private Long imageId;
        private String imageUrl;
    }

    public static responseImageDTO toDto(final Image image) {
        return responseImageDTO.builder()
                .imageId(image.getImageId())
                .imageUrl(image.getImageUrl())
                .build();
    }
}
