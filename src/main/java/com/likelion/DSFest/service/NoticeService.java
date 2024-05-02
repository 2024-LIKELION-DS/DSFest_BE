package com.likelion.DSFest.service;

import com.likelion.DSFest.aws.s3.AmazonS3Manager;
import com.likelion.DSFest.dto.NoticeDTO;
import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.entity.Image;
import com.likelion.DSFest.entity.Notice;
import com.likelion.DSFest.repository.ImageRepository;
import com.likelion.DSFest.repository.NoticeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    public String create(NoticeDTO.requestNoticeDTO noticeDTO, List<MultipartFile> multipartFiles) {
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

    public ResponseDTO<NoticeDTO.responseNoticeDTO> readAll() {
        List<Notice> notices = noticeRepository.findAll();

        List<NoticeDTO.responseNoticeDTO> noticeDTOS = new ArrayList<>();

        for(Notice n : notices) {
            NoticeDTO.responseNoticeDTO noticeDTO = NoticeDTO.toDto(n);

            //이미지 넣기
            noticeDTO.setImages(imageRepository.findByNotice_NoticeId(n.getNoticeId()));

            noticeDTOS.add(noticeDTO);
        }

        ResponseDTO<NoticeDTO.responseNoticeDTO> responseDTO = new ResponseDTO<>("모든 공지사항을 조회했습니다.", noticeDTOS);
        return responseDTO;
    }

    public ResponseDTO<NoticeDTO.responseNoticeDTO> readOne(Integer id) {
        NoticeDTO.responseNoticeDTO noticeDTO = noticeRepository.findById(id)
                .map(NoticeDTO::toDto) // Notice를 NoticeDTO로 변환하는 메소드를 호출
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공지사항을 찾을 수 없습니다."));

        noticeDTO.setImages(imageRepository.findByNotice_NoticeId(id)); // 이미지 세팅
        
        List<NoticeDTO.responseNoticeDTO> noticeDTOS = new ArrayList<>();
        noticeDTOS.add(noticeDTO);

        ResponseDTO<NoticeDTO.responseNoticeDTO> responseDTO = new ResponseDTO<>("공지사항을 조회했습니다.", noticeDTOS);
        return responseDTO;
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
