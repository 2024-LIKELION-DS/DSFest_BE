package com.likelion.DSFest.controller;

import ch.qos.logback.core.util.ContentTypeUtil;
import com.likelion.DSFest.MultipartJackson2HttpMessageConverter;
import com.likelion.DSFest.dto.NoticeDTO;
import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.annotation.MultipartConfig;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping(path="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "admin이 공지를 등록하는 api")
    @Parameters({
            @Parameter(name="responseNoticeDTO", description = "제목, 내용, 작성일, 카테고리가 들어가는 공지 입력"),
            @Parameter(name="multifiles", description = "공지에 등록할 이미지 list")
    })
    public ResponseEntity<Object> create(@RequestPart NoticeDTO.requestNoticeDTO noticeDTO,
                                         @RequestPart (required=false) List<MultipartFile> multipartFiles) {
        try {
            String message = noticeService.create(noticeDTO, multipartFiles); //글과 이미지 등록

            ResponseDTO<Object> response = ResponseDTO.builder().message(message).build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDTO<Object> response = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("read/all")
    @Operation(summary = "admin이 읽는 모든 공지")
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
    @Operation(summary = "admin이 id로 하나의 공지를 읽어옴")
    @Parameters({
            @Parameter(name = "id", description = "notice id, 읽고자하는 공지의 id")})
    public ResponseEntity<ResponseDTO> readOne(@PathVariable Long id) {
        try {
            ResponseDTO response = noticeService.readOne(id);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDTO<Object> response = ResponseDTO.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response); //오류 메시지 확인할 수 있도록
        }

    }

    @PutMapping(path="update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestPart NoticeDTO.requestNoticeDTO noticeDTO,
                                              @RequestPart (required=false) List<MultipartFile> multipartFiles) {
        try {
            //제목 내용 변경, 내부에 이미지 변경
            ResponseDTO response= noticeService.update(noticeDTO, multipartFiles, id);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDTO<Object> response = ResponseDTO.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response); //오류 메시지 확인할 수 있도록
        }
    }

}
