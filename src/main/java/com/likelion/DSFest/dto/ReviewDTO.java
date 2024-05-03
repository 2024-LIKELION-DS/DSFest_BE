package com.likelion.DSFest.dto;

import com.likelion.DSFest.entity.Review;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
public class ReviewDTO {

    private Long id;
    private String content;

    //content 값 검증
    public boolean isValid() {
        return StringUtils.isNotBlank(content);
    }

    public Review toEntity() {
        return new Review(id, content);
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class responseReviewDTO {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
    }

    public static ReviewDTO.responseReviewDTO toDto(final Review review) {
        return ReviewDTO.responseReviewDTO.builder()
                .id(review.getId())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
