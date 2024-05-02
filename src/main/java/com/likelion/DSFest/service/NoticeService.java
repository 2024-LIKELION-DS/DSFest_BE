package com.likelion.DSFest.service;

import com.likelion.DSFest.dto.NoticeDTO;
import com.likelion.DSFest.entity.Image;
import com.likelion.DSFest.entity.Notice;
import com.likelion.DSFest.repository.NoticeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    public String create(NoticeDTO noticeDTO, List<MultipartFile> multipartFiles) {
        Notice notice = NoticeDTO.toEntity(noticeDTO); //엔티티로 변경

        validate(notice); // 정보 확인

        noticeRepository.save(notice); // 글 저장

        //이미지 저장
        multipartFiles.stream().map(file ->
                Image.builder()
                        .image(file.getName())
                        .notice(notice).build()).collect(Collectors.toList()); //객체 하나하나 저장

        return "등록 성공";
    }


    // 맞게 들어온 정보인지 확인
    private void validate(final Notice notice) {
        if(notice == null) {
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }

        if(notice.getTitle() == null) {
            log.warn("Entity title cannot be null");
            throw new RuntimeException("Entity title cannot be null");
        }

        if(notice.getContent() == null) {
            log.warn("Entity content cannot be null");
            throw new RuntimeException("Entity content cannot be null");
        }
    }
}
