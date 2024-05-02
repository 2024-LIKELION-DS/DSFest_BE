package com.likelion.DSFest.service;

import com.likelion.DSFest.dto.NoticeDTO;
import com.likelion.DSFest.dto.ResponseDTO;
import com.likelion.DSFest.entity.Notice;
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

@Service
@Slf4j
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    public String create(NoticeDTO noticeDTO) {
        if (noticeDTO.getImages() != null) { //이미지가 있다면 이미지 등록 필요 s3에
            
        }
        Notice notice = NoticeDTO.toEntity(noticeDTO); //엔티티로 변경

        validate(notice); // 정보 확인

        noticeRepository.save(notice); // 저장

        return "등록 성공";
    }

    public ResponseDTO<NoticeDTO> readAll() {
        List<Notice> notices = noticeRepository.findAll();

        List<NoticeDTO> noticeDTOS = new ArrayList<>();

        for(Notice n : notices) {
            noticeDTOS.add(NoticeDTO.toDto(n));
        }

        ResponseDTO<NoticeDTO> responseDTO = new ResponseDTO<>("모든 공지사항을 조회했습니다.", noticeDTOS);
        return responseDTO;
    }

    public ResponseDTO<NoticeDTO> readOne(Integer id) {
        NoticeDTO noticeDTO = noticeRepository.findById(id)
                .map(notice -> NoticeDTO.toDto(notice)) // Notice를 NoticeDTO로 변환하는 메소드를 호출
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공지사항을 찾을 수 없습니다."));

        List<NoticeDTO> noticeDTOS = new ArrayList<>();
        noticeDTOS.add(noticeDTO);

        ResponseDTO<NoticeDTO> responseDTO = new ResponseDTO<>("공지사항을 조회했습니다.", noticeDTOS);
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
