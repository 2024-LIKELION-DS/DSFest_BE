package com.likelion.DSFest.dto;

import com.likelion.DSFest.entity.Category;
import com.likelion.DSFest.entity.Image;
import com.likelion.DSFest.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;


public class NoticeDTO {

//    private Integer id;
//    private String title;
//    private String content;
//    private Timestamp createdAt;
//    private Category category;
//    private List<Image> images; //이미지는 따로 붙일게염

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class requestNoticeDTO {
        private String title;
        private String content;
        private Timestamp createdAt;
        private Category category;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class responseNoticeDTO {
        private Integer id;
        private String title;
        private String content;
        private Timestamp createdAt;
        private Category category;
        private List<Image> images;
    }



    public static Notice toEntity(final NoticeDTO.requestNoticeDTO noticeDTO) {
        return Notice.builder()
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .category(noticeDTO.getCategory())
                .build();
    }
    public static NoticeDTO.responseNoticeDTO toDto(final Notice notice) {
        return NoticeDTO.responseNoticeDTO.builder()
                .id(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(Timestamp.valueOf(notice.getCreatedAt()))
                .category(notice.getCategory())
                // .images(null)
                .build();
    }


}
