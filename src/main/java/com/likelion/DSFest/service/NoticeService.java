package com.likelion.DSFest.service;

import com.likelion.DSFest.aws.s3.AmazonS3Manager;
import com.likelion.DSFest.dto.ImageDTO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
            List<Image> images = imageRepository.findByNotice_NoticeId(n.getNoticeId());

            List<ImageDTO.responseImageDTO> imageDTOS = images.stream().map(ImageDTO::toDto).collect(Collectors.toList());
            noticeDTO.setImages(imageDTOS);

            noticeDTOS.add(noticeDTO);
        }

        ResponseDTO<NoticeDTO.responseNoticeDTO> responseDTO = new ResponseDTO<>("모든 공지사항을 조회했습니다.", noticeDTOS);
        return responseDTO;
    }

    public ResponseDTO<NoticeDTO.responseNoticeDTO> readOne(Integer id) {
        NoticeDTO.responseNoticeDTO noticeDTO = noticeRepository.findById(id)
                .map(NoticeDTO::toDto) // Notice를 NoticeDTO로 변환하는 메소드를 호출
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공지사항을 찾을 수 없습니다."));

        List<Image> images = imageRepository.findByNotice_NoticeId(id);

        List<ImageDTO.responseImageDTO> imageDTOS = images.stream().map(ImageDTO::toDto).collect(Collectors.toList());
        noticeDTO.setImages(imageDTOS);

        List<NoticeDTO.responseNoticeDTO> noticeDTOS = new ArrayList<>();
        noticeDTOS.add(noticeDTO);

        ResponseDTO<NoticeDTO.responseNoticeDTO> responseDTO = new ResponseDTO<>("공지사항을 조회했습니다.", noticeDTOS);
        return responseDTO;
    }

    @Transactional
    public ResponseDTO<NoticeDTO.responseNoticeDTO> update(NoticeDTO.requestNoticeDTO noticeDTO, List<MultipartFile> multipartFiles,Integer id) {
        Notice originalNotice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공지사항을 찾을 수 없습니다"));

        if (noticeDTO.getTitle() == null || noticeDTO.getContent() == null || noticeDTO.getCategory() == null) { // 셋 중 하나 널이면
            log.warn("빈칸이 존재할 수 없습니다.");
            throw new RuntimeException("수정 시 빈칸이 존재할 수 없습니다.");
        }

        //수정
        originalNotice.setTitle(noticeDTO.getTitle());
        originalNotice.setContent(noticeDTO.getContent());
        originalNotice.setCategory(noticeDTO.getCategory());
        noticeRepository.save(originalNotice);

        //responseDTO 구현
        List<NoticeDTO.responseNoticeDTO> noticeDTOS = new ArrayList<>();
        NoticeDTO.responseNoticeDTO responseDTO = NoticeDTO.toDto(originalNotice);

        //이미지가 없는 경우 빼고는 다 다시 올려서 연결
        List<Image> images = imageUpdate(multipartFiles, id); //이미지 업데이트
        if (images != null) {
            List<ImageDTO.responseImageDTO> imageDTOS = images.stream().map(ImageDTO::toDto).collect(Collectors.toList());
            responseDTO.setImages(imageDTOS); //dto에 이미지들 추가
        }
        noticeDTOS.add(responseDTO);

        return ResponseDTO.<NoticeDTO.responseNoticeDTO>builder()
                .message("수정 완료")
                .data(noticeDTOS)
                .build();

    }

    @Transactional
    public List<Image> imageUpdate(List<MultipartFile> multipartFiles, Integer id) {
        imageRepository.findByNotice_NoticeId(id).forEach(image -> { //s3에서 파일 삭제
            s3Manager.deleteFile(image.getImageUrl());
        });

        imageRepository.deleteByNotice_NoticeId(id); //기존 이미지 데이터 베이스에서 모두 삭제

        if (multipartFiles == null) {
            return null;
        }
        return multipartFiles.stream().map(multipartFile -> {
                    String imageUrl = s3Manager.uploadFile(multipartFile); //이미지 새로 저장
                    Image image = Image.builder()
                            .imageUrl(imageUrl)
                            .notice(noticeRepository.findById(id).get())
                            .build();
                    imageRepository.save(image);
            return image;
        }).collect(Collectors.toList());
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
