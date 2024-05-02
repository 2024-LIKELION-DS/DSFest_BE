package com.likelion.DSFest.controller;

import ch.qos.logback.core.util.ContentTypeUtil;
import com.likelion.DSFest.MultipartJackson2HttpMessageConverter;
import com.likelion.DSFest.dto.NoticeDTO;
import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.service.NoticeService;
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
    @Autowired
    private NoticeService noticeService;

    @PostMapping(path="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create(@RequestPart NoticeDTO noticeDTO,
                                         @RequestPart (required=false) List<MultipartFile> multipartFiles) {
        try {
            String message = noticeService.create(noticeDTO);
            System.out.println(multipartFiles);

            ResponseDTO<Object> response = ResponseDTO.builder().message(message).build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDTO<Object> response = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(response);
        }
    }
}
