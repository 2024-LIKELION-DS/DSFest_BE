package com.likelion.DSFest.controller;


import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.dto.ReviewDTO;
import com.likelion.DSFest.entity.Review;
import com.likelion.DSFest.repository.ReviewRepository;
import com.likelion.DSFest.service.NoticeService;
import com.likelion.DSFest.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //글 조회
    @GetMapping("/review")
    @Operation(summary = "후기들을 전체 조회")
    public List<Review> index() {
        return reviewService.index();
    }

    //글 작성
    @PostMapping("/review")
    @Operation(summary = "후기를 작성")
    @Parameters({
            @Parameter(name = "ReviewDTO", description = "내용(content) 입력")
    })
    public ResponseEntity<Review> create(@RequestBody ReviewDTO dto) {
        Review created = reviewService.create(dto);
        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}


