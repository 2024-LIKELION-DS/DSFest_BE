package com.likelion.DSFest.dto;

import com.likelion.DSFest.entity.Category;
import com.likelion.DSFest.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;


public class NoticeDTO {

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class requestNoticeDTO {
        private String title;
        private String content;
        private Timestamp createdAt;
        private String categoryName;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class responseNoticeDTO {
        private Long id;
        private String title;
        private String content;
        private Timestamp createdAt;
        private Category category;
        private List<ImageDTO.responseImageDTO> images;
    }



    public static Notice toEntity(final NoticeDTO.requestNoticeDTO noticeDTO, final Category category) {
        return Notice.builder()
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .category(category)
                .build();
    }
    public static NoticeDTO.responseNoticeDTO toDto(final Notice notice) {
        return NoticeDTO.responseNoticeDTO.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(Timestamp.valueOf(notice.getCreatedAt()))
                .category(notice.getCategory())
                // .images(null)
                .build();
    }


}
