package com.ktb.ktb_community.controller;

import com.ktb.ktb_community.common.advice.ApiResponse;
import com.ktb.ktb_community.dto.PostLikeResponseDto;
import com.ktb.ktb_community.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostLikeResponseDto>> createPostLike(
            @RequestParam("postid") Long postId,
            Principal principal
    ){

        PostLikeResponseDto dto = postLikeService.createLike(postId, principal.getName());

        ApiResponse<PostLikeResponseDto> response = ApiResponse.success(
                "post_like_success",
                dto
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deletePostLike(
            @RequestParam("postid") Long postId,
            Principal principal
    ){

        PostLikeResponseDto dto = postLikeService.deleteLike(postId, principal.getName());

        ApiResponse<Void> response = ApiResponse.success("post_like_success");
        return ResponseEntity.ok(response);
    }


}
