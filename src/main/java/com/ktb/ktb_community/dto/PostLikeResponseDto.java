package com.ktb.ktb_community.dto;

import lombok.Data;

@Data
public class PostLikeResponseDto {

    private Integer likeCount;
    private Boolean isLiked;

    public PostLikeResponseDto(Integer likeCount, Boolean isLiked) {
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
