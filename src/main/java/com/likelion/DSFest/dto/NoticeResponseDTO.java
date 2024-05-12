package com.likelion.DSFest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@Data
public class NoticeResponseDTO<T> { //자바 generic 이용함, 어떤 원소의 타입 리스트도 반환할 수 있도록 함.
    private String message; // 성공 혹은 오류 메시지
    private Long totalNum;
    private List<T> data; //

    public NoticeResponseDTO(String message, Long totalNum, List<T> data) {
        this.message = message;
        this.totalNum = totalNum;
        this.data = data;
    }

}
