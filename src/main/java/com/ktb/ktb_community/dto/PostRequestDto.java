package com.ktb.ktb_community.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostRequestDto {

    // 공백 안됨
    // 글자 수 제한
    private String title;
    // 공백 안됨
    // 글자 수 제한?
    private String content;
}
