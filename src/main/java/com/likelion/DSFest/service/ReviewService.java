package com.likelion.DSFest.service;


import com.likelion.DSFest.aws.s3.AmazonS3Manager;
import com.likelion.DSFest.dto.ReviewDTO;
import com.likelion.DSFest.repository.ImageRepository;
import com.likelion.DSFest.repository.NoticeRepository;
import com.likelion.DSFest.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import com.likelion.DSFest.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    private ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    //글 조회
    public List<Review> index(){
        return reviewRepository.findAll();
    }

    //글 작성
    public Review create(@RequestBody ReviewDTO dto){
        Review review = dto.toEntity();
        if (review.getId() != null) {
            return null;
        }
        return reviewRepository.save(review);
    }
}
