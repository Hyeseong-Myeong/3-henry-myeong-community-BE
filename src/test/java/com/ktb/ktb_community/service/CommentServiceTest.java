package com.ktb.ktb_community.service;

import com.ktb.ktb_community.dto.CommentRequestDto;
import com.ktb.ktb_community.dto.CommentResponseDto;
import com.ktb.ktb_community.entity.Comment;
import com.ktb.ktb_community.entity.Post;
import com.ktb.ktb_community.entity.User;
import com.ktb.ktb_community.exception.NoPermissionException;
import com.ktb.ktb_community.exception.NotFoundException;
import com.ktb.ktb_community.repository.CommentRepository;
import com.ktb.ktb_community.repository.PostRepository;
import com.ktb.ktb_community.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("testuser")
                .build();
        ReflectionTestUtils.setField(user, "userId", 1L);

        post = Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(user)
                .build();
        ReflectionTestUtils.setField(post, "postId", 1L);
    }

    private CommentRequestDto createCommentRequestDto(String content) {
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent(content);
        return requestDto;
    }

    @Test
    @DisplayName("댓글 생성 - 성공")
    void createComment_Success() {
        // given
        String userId = "1";
        Long postId = 1L;
        CommentRequestDto requestDto = createCommentRequestDto("새로운 댓글 내용");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when
        CommentResponseDto responseDto = commentService.create(requestDto, userId, postId);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getContent()).isEqualTo("새로운 댓글 내용");
        assertThat(responseDto.getIsAuthor()).isTrue();
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 - 실패 (사용자를 찾을 수 없음)")
    void createComment_Fail_UserNotFound() {
        // given
        String userId = "999";
        Long postId = 1L;
        CommentRequestDto requestDto = createCommentRequestDto("새로운 댓글 내용");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> commentService.create(requestDto, userId, postId));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 - 실패 (게시글을 찾을 수 없음)")
    void createComment_Fail_PostNotFound() {
        // given
        String userId = "1";
        Long postId = 999L;
        CommentRequestDto requestDto = createCommentRequestDto("새로운 댓글 내용");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> commentService.create(requestDto, userId, postId));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    void updateComment_Success() {
        // given
        String userId = "1";
        Long commentId = 1L;
        CommentRequestDto requestDto = createCommentRequestDto("수정된 댓글 내용");
        Comment existingComment = new Comment("원본 내용", user, post);
        Comment spyComment = spy(existingComment);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(spyComment));

        // when
        CommentResponseDto responseDto = commentService.update(requestDto, userId, commentId);

        // then
        assertThat(responseDto.getContent()).isEqualTo("수정된 댓글 내용");
        verify(spyComment, times(1)).update("수정된 댓글 내용");
    }

    @Test
    @DisplayName("댓글 수정 - 실패 (권한 없음)")
    void updateComment_Fail_NoPermission() {
        // given
        String otherUserId = "2";
        Long commentId = 1L;
        CommentRequestDto requestDto = createCommentRequestDto("수정된 댓글 내용");
        User otherUser = User.builder().email("other@test.com").password("p").nickname("other").build();
        ReflectionTestUtils.setField(otherUser, "userId", 2L);
        Comment existingComment = new Comment("원본 내용", user, post);

        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // when & then
        assertThrows(NoPermissionException.class, () -> commentService.update(requestDto, otherUserId, commentId));
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void deleteComment_Success() {
        // given
        String userId = "1";
        Long commentId = 1L;
        Comment existingComment = new Comment("삭제될 댓글", user, post);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // when
        Boolean result = commentService.deleteCommentById(commentId, userId);

        // then
        assertTrue(result);
        verify(commentRepository, times(1)).delete(existingComment);
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 (권한 없음)")
    void deleteComment_Fail_NoPermission() {
        // given
        String otherUserId = "2";
        Long commentId = 1L;
        User otherUser = User.builder().email("other@test.com").password("p").nickname("other").build();
        ReflectionTestUtils.setField(otherUser, "userId", 2L);
        Comment existingComment = new Comment("삭제될 댓글", user, post);

        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // when & then
        assertThrows(NoPermissionException.class, () -> commentService.deleteCommentById(commentId, otherUserId));
        verify(commentRepository, never()).delete(any(Comment.class));
    }
}
