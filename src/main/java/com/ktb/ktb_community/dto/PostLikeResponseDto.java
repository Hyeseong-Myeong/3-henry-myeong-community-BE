package com.ktb.ktb_community.dto;

import com.ktb.ktb_community.entity.PostLike;
import lombok.Data;

@Data
public class PostLikeResponseDto {

    Integer likeCount;
    Boolean isLiked;

    public PostLikeResponseDto(Integer likeCount, Boolean isLiked) {
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
