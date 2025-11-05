package com.ktb.ktb_community.controller;

import com.ktb.ktb_community.common.advice.ApiResponse;
import com.ktb.ktb_community.dto.PostPageResponseDto;
import com.ktb.ktb_community.dto.PostRequestDto;
import com.ktb.ktb_community.dto.PostResponseDto;
import com.ktb.ktb_community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(@RequestBody PostRequestDto postRequestDto, Principal principal) {

        PostResponseDto postResponseDto = postService.create(postRequestDto, principal.getName());

        ApiResponse<PostResponseDto> response = ApiResponse.success(
                "create_post_success",
                postResponseDto
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(@PathVariable("postId") Long postId, Principal principal) {

        PostResponseDto postResponseDto = postService.getPostById(postId,principal.getName());

        ApiResponse<PostResponseDto> response = ApiResponse.success(
                "get_post_success",
                postResponseDto
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PostPageResponseDto>> getPosts(
            @RequestParam(value = "cursor", required = false) Integer cursor,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size
    ){

        PostPageResponseDto postPageResponseDto =  postService.getPosts(cursor, size);

        ApiResponse<PostPageResponseDto> response = ApiResponse.success(
                "get_posts_success",
                postPageResponseDto
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody PostRequestDto postRequestDto,
            Principal principal
    ) {

        PostResponseDto postResponseDto = postService.updatePost(postRequestDto, postId, principal.getName());
        ApiResponse<PostResponseDto> response = ApiResponse.success(
                "update_post_success",
                postResponseDto
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable("postId") Long postId, Principal principal) {

        postService.deletePostById(postId, principal.getName());
        ApiResponse<Void> response = ApiResponse.success("delete_post_success");
        return ResponseEntity.ok(response);
    }
}
