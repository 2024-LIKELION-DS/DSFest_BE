package com.likelion.DSFest.controller;

import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserNoticeController {
    private final NoticeService noticeService;

    public UserNoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("read")
    @Operation(summary = "사용자 입장에서의 공지 페이지네이션 적용")
    @Parameters({
            @Parameter(name = "page", required = false, description = "page 번호"),
            @Parameter(name = "size", required = false, description = "한번에 볼 페이지 사이즈")
    })
    public ResponseEntity<ResponseDTO> readAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size
    ) {
        try {
            ResponseDTO response = noticeService.readPagenation(page, size);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            ResponseDTO<Object> response = ResponseDTO.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response); //오류 메시지 확인할 수 있도록
        }

    }

    @GetMapping("read/{id}")
    @Operation(summary = "사용자 입장에서의 조회하고자 하는 id로 조회한 공지")
    @Parameters({
            @Parameter(name = "id", description = "notice id로 조회하고자 하는 공지의 id")
    })
    public ResponseEntity<ResponseDTO> readOne(@PathVariable Long id) {
        try {
            ResponseDTO response = noticeService.readOne(id);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDTO<Object> response = ResponseDTO.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response); //오류 메시지 확인할 수 있도록
        }

    }
}
