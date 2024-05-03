package com.likelion.DSFest.controller;

import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserNoticeController {
    private final NoticeService noticeService;

    public UserNoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("read/all")
    @Operation(summary = "사용자 입장에서의 모든 공지")
    public ResponseEntity<ResponseDTO> readAll() {
        try {
            ResponseDTO response = noticeService.readAll();
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
