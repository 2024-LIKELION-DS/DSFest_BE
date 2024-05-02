package com.likelion.DSFest.controller;

import com.likelion.DSFest.dto.NoticeDTO;
import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody NoticeDTO noticeDTO) {
        try {
            String message = noticeService.create(noticeDTO);

            ResponseDTO<Object> response = ResponseDTO.builder().message(message).build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDTO<Object> response = ResponseDTO.builder().message(e.getMessage()).build();

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> readAll() {
        ResponseDTO response = noticeService.readAll();
        return ResponseEntity.ok().body(response);
    }
}
