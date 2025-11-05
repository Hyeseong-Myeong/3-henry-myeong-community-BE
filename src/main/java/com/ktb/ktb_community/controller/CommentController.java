package com.ktb.ktb_community.controller;

import com.ktb.ktb_community.common.advice.ApiResponse;
import com.ktb.ktb_community.dto.CommentPageResponseDto;
import com.ktb.ktb_community.dto.CommentRequestDto;
import com.ktb.ktb_community.dto.CommentResponseDto;
import com.ktb.ktb_community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @RequestParam(value = "postid") Long postId,
            @RequestBody CommentRequestDto commentRequestDto,
            Principal principal
    ) {

        CommentResponseDto responseDto = commentService.create(commentRequestDto, principal.getName(), postId);
        ApiResponse<CommentResponseDto> response =  ApiResponse.success(
                "create_comment_success",
                responseDto
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> getComment(
            @PathVariable Long commentId,
            Principal principal
    ){

        CommentResponseDto commentResponseDto = commentService.getCommentById(commentId, principal.getName());
        ApiResponse<CommentResponseDto> response =  ApiResponse.success(
                "get_comment_success",
                commentResponseDto
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CommentPageResponseDto>> getComments(
            @RequestParam("postid") Long postId,
            @RequestParam(value = "cursor", required = false) Integer cursor,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            Principal principal
    ){

        CommentPageResponseDto commentPageResponseDto = commentService.getCommentsByPostId(postId, cursor, size, principal.getName());

        ApiResponse<CommentPageResponseDto> response =  ApiResponse.success(
                "get_comments_success",
                commentPageResponseDto
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequestDto commentRequestDto,
            Principal principal
    ){

        CommentResponseDto responseDto = commentService.update(commentRequestDto, principal.getName(), commentId);

        ApiResponse<CommentResponseDto> response =  ApiResponse.success(
                "update_comment_success",
                responseDto
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("commentId") Long commentId,
            Principal principal
    ){

        commentService.deleteCommentById(commentId, principal.getName());

        ApiResponse<Void> response =  ApiResponse.success("delete_comment_success");
        return ResponseEntity.ok(response);
    }
}
