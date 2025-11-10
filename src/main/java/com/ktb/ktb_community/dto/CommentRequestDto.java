package com.ktb.ktb_community.dto;

import lombok.Data;

@Data
public class CommentRequestDto {

    //공백이면 안됨
    //글자수 제한?
    private String content;
}
