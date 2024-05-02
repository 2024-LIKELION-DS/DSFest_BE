package com.likelion.DSFest.service;

import com.likelion.DSFest.aws.s3.AmazonS3Manager;
import com.likelion.DSFest.dto.NoticeDTO;
import com.likelion.DSFest.entity.Image;
import com.likelion.DSFest.entity.Notice;
import com.likelion.DSFest.repository.ImageRepository;
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

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AmazonS3Manager s3Manager;

    public String create(NoticeDTO noticeDTO, List<MultipartFile> multipartFiles) {
        Notice notice = NoticeDTO.toEntity(noticeDTO); //엔티티로 변경

        validate(notice); // 정보 확인

        noticeRepository.save(notice); // 글 저장

        //이미지 저장
        multipartFiles.forEach(multipartFile -> {
            try {
                String fileurl = s3Manager.uploadFile(multipartFile);
                Image image = Image.builder()
                        .imageUrl(fileurl) // s3에 업로드한 이미지 url 받아서 저장
                        .notice(notice)
                        .build();
                imageRepository.save(image); // 이미지 저장
            } catch (Exception e) { // 객체 저장 시 에러 발생 => 예외 처리
                log.error("Error occurred while saving image: {}", e.getMessage());
                throw new RuntimeException("Failed to save image");
            }
        });

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
