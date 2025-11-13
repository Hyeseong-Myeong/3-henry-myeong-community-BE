package com.ktb.ktb_community.service;

import com.ktb.ktb_community.dto.CommentPageResponseDto;
import com.ktb.ktb_community.dto.CommentRequestDto;
import com.ktb.ktb_community.dto.CommentResponseDto;
import com.ktb.ktb_community.dto.CursorDto;
import com.ktb.ktb_community.entity.Comment;
import com.ktb.ktb_community.entity.Post;
import com.ktb.ktb_community.entity.User;
import com.ktb.ktb_community.exception.NoPermissionException;
import com.ktb.ktb_community.exception.NotFoundException;
import com.ktb.ktb_community.repository.CommentRepository;
import com.ktb.ktb_community.repository.PostRepository;
import com.ktb.ktb_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDto create(CommentRequestDto commentRequestDto, String userId, Long postId) {

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        Comment comment = new Comment(commentRequestDto.getContent(), user, post);
        commentRepository.save(comment);

        return CommentResponseDto.from(comment, Boolean.TRUE);
    }

    public CommentResponseDto getCommentById(Long commentId, String userId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));
        Boolean isAuthor = comment.getUser().getUserId().equals(Long.valueOf(userId));

        return CommentResponseDto.from(comment, isAuthor);
    }

    public CommentPageResponseDto getCommentsByPostId(Long postId, Integer cursor, int size, String userId) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "commentId"));

        //post 검증
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        Slice<Comment> commentSlice = (cursor == null)
                ? commentRepository.findByPost_PostIdOrderByCommentIdAsc(postId, pageable)
                : commentRepository.findByPost_PostIdAndCommentIdGreaterThanOrderByCommentIdAsc(postId, Long.valueOf(cursor), pageable);

        List<CommentResponseDto> commentResponseDtoList = commentSlice.getContent().stream()
                .map(comment -> {
                    Boolean isAuthor = comment.getUser().getUserId().equals(Long.valueOf(userId));
                    return CommentResponseDto.from(comment, isAuthor);
                })
                .toList();

        Long nextCursor = null;
        if (!commentResponseDtoList.isEmpty()) {
            nextCursor = commentResponseDtoList.get(commentResponseDtoList.size() - 1).getCommentId();
        }

        CursorDto cursorDto = new CursorDto(nextCursor, commentSlice.hasNext());
        return new CommentPageResponseDto(commentResponseDtoList, cursorDto);
    }

    @Transactional
    public CommentResponseDto update(CommentRequestDto commentRequestDto, String userId, Long commentId) {

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new NotFoundException("USER_NOTFOUND"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("COMMENT_NOTFOUND"));

        //댓글 작성자가 아닌 경우 수정 불가
        if(!user.equals(comment.getUser())) {
            throw new NoPermissionException("NO_PERMISSION");
        }

        comment.update(commentRequestDto.getContent());

        return CommentResponseDto.from(comment, Boolean.TRUE);
    }

    @Transactional
    public Boolean deleteCommentById(Long commentId, String userId){

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));

        //권한 확인
        if(!user.equals(comment.getUser())) {
            throw new NoPermissionException("NO_PERMISSION");
        }

        commentRepository.delete(comment);
        return true;
    }
}
