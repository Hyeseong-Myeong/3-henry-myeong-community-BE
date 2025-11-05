package com.ktb.ktb_community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class PostPageResponseDto {

    private List<PostResponseDto> postList;
    private CursorDto cursor;
}
