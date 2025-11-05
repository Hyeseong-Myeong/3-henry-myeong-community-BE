package com.ktb.ktb_community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommentPageResponseDto {

    private List<CommentResponseDto> commentList;
    private CursorDto cursor;
}
