package com.ktb.ktb_community.dto;

import com.ktb.ktb_community.entity.Comment;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CommentResponseDto {

    private Long commentId;

    private UserResponseDto user;

    private String content;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private Boolean isAuthor;

    public CommentResponseDto(Long commentId, UserResponseDto user, String content, Timestamp createdAt, Timestamp updatedAt, Boolean isAuthor) {

        this.commentId = commentId;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAuthor = isAuthor;
    }

    public static CommentResponseDto from(Comment comment, Boolean isAuthor) {

        return new CommentResponseDto(
                comment.getCommentId(),
                UserResponseDto.from(comment.getUser()),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                isAuthor
        );
    }
}
