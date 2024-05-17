package com.likelion.DSFest.service;

import com.likelion.DSFest.aws.s3.AmazonS3Manager;
import com.likelion.DSFest.dto.ImageDTO;
import com.likelion.DSFest.dto.NoticeDTO;
import com.likelion.DSFest.dto.NoticeResponseDTO;
import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.entity.Image;
import com.likelion.DSFest.entity.Notice;
import com.likelion.DSFest.repository.CategoryRepository;
import com.likelion.DSFest.repository.ImageRepository;
import com.likelion.DSFest.repository.NoticeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

// 페이지네이션
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
public class NoticeService {
    private final NoticeRepository noticeRepository;

    private final ImageRepository imageRepository;

    private final AmazonS3Manager s3Manager;
    private final CategoryRepository categoryRepository;

    public NoticeService(AmazonS3Manager s3Manager, ImageRepository imageRepository, NoticeRepository noticeRepository, CategoryRepository categoryRepository) {
        this.s3Manager = s3Manager;
        this.noticeRepository = noticeRepository;
        this.imageRepository = imageRepository;
        this.categoryRepository = categoryRepository;
    }

    public String create(NoticeDTO.requestNoticeDTO noticeDTO, List<MultipartFile> multipartFiles) {
        Notice notice = NoticeDTO.toEntity(noticeDTO, categoryRepository.findByName(noticeDTO.getCategoryName())); //엔티티로 변경

        validate(notice); // 정보 확인

        noticeRepository.save(notice); // 글 저장

        //이미지 저장
        if (multipartFiles != null) {
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
        }
        else {
            log.warn("No files provided");
        }


        return "등록 성공";
    }
    public String createWithNoImage(NoticeDTO.requestNoticeDTO noticeDTO) {
        Notice notice = NoticeDTO.toEntity(noticeDTO, categoryRepository.findByName(noticeDTO.getCategoryName())); //엔티티로 변경

        validate(notice); // 정보 확인

        noticeRepository.save(notice); // 글 저장

        return "이미지 없이 등록 성공";
    }

    public NoticeResponseDTO<NoticeDTO.responseNoticeDTO> readPagenation(Integer page, Integer size) {
        // page 1부터 시작할 수 있도록 page + 1 설정
        Pageable pageable = PageRequest.of(page - 1, size);

        // DB에서 페이지네이션된 데이터 가져옴
        Page<Notice> noticePage = noticeRepository.findAllByOrderByCreatedAtDesc(pageable);

        // Page 객체에서 List 추출
        List<Notice> notices = noticePage.getContent();

        // DTO 리스트 생성
        List<NoticeDTO.responseNoticeDTO> noticeDTOS = new ArrayList<>();
        for (Notice notice : notices) {
            NoticeDTO.responseNoticeDTO noticeDTO = NoticeDTO.toDto(notice);

            // 이미지 넣기
            List<Image> images = imageRepository.findByNotice_Id(notice.getId());
            List<ImageDTO.responseImageDTO> imageDTOS = images.stream().map(ImageDTO::toDto).collect(Collectors.toList());
            noticeDTO.setImages(imageDTOS);

            // 이미지 개수 넣기
            noticeDTO.setImageNum(images.size());

            noticeDTOS.add(noticeDTO);
        }

        return new NoticeResponseDTO(
                "page : " + page + ", size : " + size + " 공지사항을 조회했습니다.",
                getTotalNoticeCount(),
                noticeDTOS
        );

    }

    public ResponseDTO<NoticeDTO.responseNoticeDTO> readAll() {
        List<Notice> notices = noticeRepository.findAllByOrderByCreatedAtDesc();

        List<NoticeDTO.responseNoticeDTO> noticeDTOS = new ArrayList<>();

        for(Notice n : notices) {
            NoticeDTO.responseNoticeDTO noticeDTO = NoticeDTO.toDto(n);

            //이미지 넣기
            List<Image> images = imageRepository.findByNotice_Id(n.getId());

            List<ImageDTO.responseImageDTO> imageDTOS = images.stream().map(ImageDTO::toDto).collect(Collectors.toList());
            noticeDTO.setImages(imageDTOS);

            noticeDTOS.add(noticeDTO);
        }

        return ResponseDTO.<NoticeDTO.responseNoticeDTO>builder().message("모든 공지사항을 조회했습니다.").data(noticeDTOS).build();
    }

    public ResponseDTO<NoticeDTO.responseNoticeDTO> readOne(Long id) {
        NoticeDTO.responseNoticeDTO noticeDTO = noticeRepository.findById(id)
                .map(NoticeDTO::toDto) // Notice를 NoticeDTO로 변환하는 메소드를 호출
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공지사항을 찾을 수 없습니다."));

        List<Image> images = imageRepository.findByNotice_Id(id);

        List<ImageDTO.responseImageDTO> imageDTOS = images.stream().map(ImageDTO::toDto).collect(Collectors.toList());
        noticeDTO.setImages(imageDTOS);

        noticeDTO.setImageNum(images.size());

        List<NoticeDTO.responseNoticeDTO> noticeDTOS = new ArrayList<>();
        noticeDTOS.add(noticeDTO);

        return ResponseDTO.<NoticeDTO.responseNoticeDTO>builder().message("공지사항을 조회했습니다.").data(noticeDTOS).build();
    }

    @Transactional
    public ResponseDTO<NoticeDTO.responseNoticeDTO> update(NoticeDTO.requestNoticeDTO noticeDTO, List<MultipartFile> multipartFiles,Long id) {
        Notice originalNotice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공지사항을 찾을 수 없습니다"));

        if (noticeDTO.getTitle() == null || noticeDTO.getContent() == null || noticeDTO.getCategoryName() == null) { // 셋 중 하나 널이면
            log.warn("빈칸이 존재할 수 없습니다.");
            throw new RuntimeException("수정 시 빈칸이 존재할 수 없습니다.");
        }

        //수정
        originalNotice.setTitle(noticeDTO.getTitle());
        originalNotice.setContent(noticeDTO.getContent());
        originalNotice.setCategory(categoryRepository.findByName(noticeDTO.getCategoryName()));
        noticeRepository.save(originalNotice);

        //responseDTO 구현
        List<NoticeDTO.responseNoticeDTO> noticeDTOS = new ArrayList<>();
        NoticeDTO.responseNoticeDTO responseDTO = NoticeDTO.toDto(originalNotice);

        //이미지가 없는 경우 빼고는 다 다시 올려서 연결
        List<Image> images = imageUpdate(multipartFiles, id); //이미지 업데이트
        if (images != null) {
            List<ImageDTO.responseImageDTO> imageDTOS = images.stream().map(ImageDTO::toDto).collect(Collectors.toList());
            responseDTO.setImages(imageDTOS); //dto에 이미지들 추가
            responseDTO.setImageNum(images.size());
            noticeDTOS.add(responseDTO);
        }

        return ResponseDTO.<NoticeDTO.responseNoticeDTO>builder()
                .message("수정 완료")
                .data(noticeDTOS)
                .build();

    }

    @Transactional
    public List<Image> imageUpdate(List<MultipartFile> multipartFiles, Long id) {
        imageRepository.findByNotice_Id(id).forEach(image -> { //s3에서 파일 삭제
            s3Manager.deleteFile(image.getImageUrl());
        });

        imageRepository.deleteByNotice_Id(id); //기존 이미지 데이터 베이스에서 모두 삭제

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

    @Transactional
    public ResponseDTO<NoticeDTO.responseNoticeDTO> delete(Long id) {

        noticeRepository.deleteById(id);

        return readAll();
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

    public Long getTotalNoticeCount() {
        return noticeRepository.count();
    }
}
