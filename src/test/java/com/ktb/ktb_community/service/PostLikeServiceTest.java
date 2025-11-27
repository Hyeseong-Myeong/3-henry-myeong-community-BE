package com.ktb.ktb_community.service;

import com.ktb.ktb_community.dto.PostLikeResponseDto;
import com.ktb.ktb_community.entity.Post;
import com.ktb.ktb_community.entity.PostLike;
import com.ktb.ktb_community.entity.User;
import com.ktb.ktb_community.exception.DuplicatedException;
import com.ktb.ktb_community.exception.NotFoundException;
import com.ktb.ktb_community.repository.PostLikeRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @InjectMocks
    private PostLikeService postLikeService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

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

    @Test
    @DisplayName("좋아요 추가 - 성공")
    void createLike_Success() {
        // given
        Long postId = 1L;
        String userId = "1";

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postLikeRepository.findByPost_PostIdAndUser_UserId(postId, 1L)).thenReturn(Optional.empty());
        when(postLikeRepository.countByPost_PostId(postId)).thenReturn(10);

        // when
        PostLikeResponseDto response = postLikeService.createLike(postId, userId);

        // then
        assertThat(response.getLikeCount()).isEqualTo(10);
        assertThat(response.getIsLiked()).isTrue();
        verify(postLikeRepository, times(1)).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 추가 - 실패 (이미 좋아요 누름)")
    void createLike_Fail_AlreadyLiked() {
        // given
        Long postId = 1L;
        String userId = "1";
        PostLike existingLike = PostLike.builder().post(post).user(user).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postLikeRepository.findByPost_PostIdAndUser_UserId(postId, 1L)).thenReturn(Optional.of(existingLike));

        // when & then
        assertThrows(DuplicatedException.class, () -> postLikeService.createLike(postId, userId));
        verify(postLikeRepository, never()).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 취소 - 성공")
    void deleteLike_Success() {
        // given
        Long postId = 1L;
        String userId = "1";
        PostLike existingLike = PostLike.builder().post(post).user(user).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postLikeRepository.findByPost_PostIdAndUser_UserId(postId, 1L)).thenReturn(Optional.of(existingLike));
        when(postLikeRepository.countByPost_PostId(postId)).thenReturn(9);

        // when
        PostLikeResponseDto response = postLikeService.deleteLike(postId, userId);

        // then
        assertThat(response.getLikeCount()).isEqualTo(9);
        assertThat(response.getIsLiked()).isFalse();
        verify(postLikeRepository, times(1)).delete(existingLike);
    }

    @Test
    @DisplayName("좋아요 취소 - 실패 (좋아요를 누른 적 없음)")
    void deleteLike_Fail_LikeNotFound() {
        // given
        Long postId = 1L;
        String userId = "1";

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postLikeRepository.findByPost_PostIdAndUser_UserId(postId, 1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> postLikeService.deleteLike(postId, userId));
        verify(postLikeRepository, never()).delete(any(PostLike.class));
    }
}
