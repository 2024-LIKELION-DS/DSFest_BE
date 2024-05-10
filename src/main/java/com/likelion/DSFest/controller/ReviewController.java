package com.likelion.DSFest.controller;

import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.dto.ReviewDTO;
import com.likelion.DSFest.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //후기 전체 조회
    @GetMapping("/review")
    @Operation(summary = "후기들을 전체 조회")
    public ResponseEntity<ResponseDTO> read() {
        try {
            ResponseDTO response = reviewService.read();
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            ResponseDTO<Object> response = ResponseDTO.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }

    }

    //후기 작성
    @PostMapping("/review")
    @Operation(summary = "후기를 작성")
    @Parameters({
            @Parameter(name = "ReviewDTO", description = "내용(content) 입력")
    })
    public ResponseEntity<ResponseDTO<ReviewDTO.responseReviewDTO>> create(@RequestBody ReviewDTO dto) {
        try {
            ResponseDTO<ReviewDTO.responseReviewDTO> serviceResponse = reviewService.create(dto);
            return ResponseEntity.ok().body(serviceResponse);
        } catch (Exception e) {
            ResponseDTO<ReviewDTO.responseReviewDTO> errorResponse = ResponseDTO.<ReviewDTO.responseReviewDTO>builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @GetMapping("/healthcheck")
    public String healthcheck() {
    	return "OK";
    }


}


