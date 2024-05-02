package com.likelion.DSFest.dto;

import com.likelion.DSFest.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class ResponseDTO<T> { //자바 generic 이용함, 어떤 원소의 타입 리스트도 반환할 수 있도록 함.
    private String message; // 성공 혹은 오류 메시지
    private List<T> data; //
}
