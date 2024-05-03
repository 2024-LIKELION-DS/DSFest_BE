package com.likelion.DSFest.dto;

import com.likelion.DSFest.entity.Review;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ReviewDTO {

    private Long id;
    private String content;

    public Review toEntity() {
        return new Review(id, content);
    }
}
