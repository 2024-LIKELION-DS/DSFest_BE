package com.likelion.DSFest.dto;

import com.likelion.DSFest.entity.Category;
import com.likelion.DSFest.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeDTO {
    private Integer id;
    private String title;
    private String content;
    private Timestamp createdAt;
    private Category category;
    private List<String> images; //이미지는 따로 붙일게염

    public NoticeDTO(final Notice notice) {
        Notice.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .category(notice.getCategory())
                .build();
    }

    public static Notice toEntity(final NoticeDTO noticeDTO) {
        return Notice.builder()
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .category(noticeDTO.getCategory())
                .build();
    }
    public static NoticeDTO toDto(final Notice notice) {
        return NoticeDTO.builder()
                .id(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(Timestamp.valueOf(notice.getCreatedAt()))
                .category(notice.getCategory())
                .images(null)
                .build();
    }


}
