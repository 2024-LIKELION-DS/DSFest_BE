package com.likelion.DSFest.service;

import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.dto.ReviewDTO;
import com.likelion.DSFest.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import com.likelion.DSFest.entity.Review;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReviewService {

    private ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    //후기 조회
    public ResponseDTO<ReviewDTO.responseReviewDTO> read() {
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewDTO.responseReviewDTO> reviewDTOS = new ArrayList<>();

        for(Review n : reviews) {
            ReviewDTO.responseReviewDTO reviewDTO = ReviewDTO.toDto(n);
            reviewDTOS.add(reviewDTO);
        }
        return ResponseDTO.<ReviewDTO.responseReviewDTO>builder().message("모든 후기를 조회했습니다.").data(reviewDTOS).build();
    }

    // 후기 작성
    public ResponseDTO<ReviewDTO.responseReviewDTO> create(ReviewDTO dto){
        // content 값 검증
        if (!dto.isValid()) {
            return ResponseDTO.<ReviewDTO.responseReviewDTO>builder().message("content를 입력해주세요.").build();
        }

        Review review = dto.toEntity();
        review = reviewRepository.save(review);
        ReviewDTO.responseReviewDTO responseDTO = ReviewDTO.toDto(review);

        List<ReviewDTO.responseReviewDTO> responseData = new ArrayList<>();
        responseData.add(responseDTO);

        return ResponseDTO.<ReviewDTO.responseReviewDTO>builder().message("후기 등록 성공").data(responseData).build();
    }
}
