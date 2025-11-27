package com.ktb.ktb_community.service;

import com.ktb.ktb_community.dto.PostPageResponseDto;
import com.ktb.ktb_community.dto.PostRequestDto;
import com.ktb.ktb_community.dto.PostResponseDto;
import com.ktb.ktb_community.entity.Post;
import com.ktb.ktb_community.entity.User;
import com.ktb.ktb_community.exception.NoPermissionException;
import com.ktb.ktb_community.exception.NotFoundException;
import com.ktb.ktb_community.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostImageRepository postImageRepository;

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

    private PostRequestDto createPostRequestDto(String title, String content) {
        PostRequestDto requestDto = new PostRequestDto();
        requestDto.setTitle(title);
        requestDto.setContent(content);
        return requestDto;
    }

    @Test
    @DisplayName("게시글 생성 - 성공")
    void createPost_Success() {
        // given
        String userId = "1";
        PostRequestDto requestDto = createPostRequestDto("테스트 제목", "테스트 내용");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        lenient().when(commentRepository.countByPost_PostId(anyLong())).thenReturn(0);

        // when
        PostResponseDto responseDto = postService.create(requestDto, userId);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo("테스트 제목");
        assertThat(responseDto.getContent()).isEqualTo("테스트 내용");
        assertThat(responseDto.getIsAuthor()).isTrue();

        verify(userRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 생성 - 실패 (사용자를 찾을 수 없음)")
    void createPost_Fail_UserNotFound() {
        // given
        String userId = "999";
        PostRequestDto requestDto = createPostRequestDto("테스트 제목", "테스트 내용");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> postService.create(requestDto, userId));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 단건 조회 - 성공")
    void getPostById_Success() {
        // given
        Long postId = 1L;
        String userId = "1";
        Post spyPost = spy(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(spyPost));
        when(postLikeRepository.countByPost_PostId(postId)).thenReturn(5);
        when(postImageRepository.findAllByPost_PostId(postId)).thenReturn(Collections.emptyList());
        when(postLikeRepository.findByPost_PostIdAndUser_UserId(postId, 1L)).thenReturn(Optional.empty());
        when(commentRepository.countByPost_PostId(postId)).thenReturn(3);

        // when
        PostResponseDto responseDto = postService.getPostById(postId, userId);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getPostId()).isEqualTo(postId);
        assertThat(responseDto.getLikeCount()).isEqualTo(5);
        assertThat(responseDto.getCommentCount()).isEqualTo(3);
        assertThat(responseDto.getIsAuthor()).isTrue();
        verify(spyPost, times(1)).incrementViewCount();
    }

    @Test
    @DisplayName("게시글 단건 조회 - 실패 (게시글을 찾을 수 없음)")
    void getPostById_Fail_PostNotFound() {
        // given
        Long postId = 999L;
        String userId = "1";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> postService.getPostById(postId, userId));
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void updatePost_Success() {
        // given
        Long postId = 1L;
        String userId = "1";
        Post spyPost = spy(post);
        PostRequestDto requestDto = createPostRequestDto("수정된 제목", "수정된 내용");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(spyPost));
        when(postRepository.save(any(Post.class))).thenReturn(spyPost);

        // when
        postService.updatePost(requestDto, postId, userId);

        // then
        verify(spyPost, times(1)).update("수정된 제목", "수정된 내용");
        verify(postRepository, times(1)).save(spyPost);
    }

    @Test
    @DisplayName("게시글 수정 - 실패 (게시글을 찾을 수 없음)")
    void updatePost_Fail_PostNotFound() {
        // given
        Long postId = 999L;
        String userId = "1";
        PostRequestDto requestDto = createPostRequestDto("수정된 제목", "수정된 내용");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> postService.updatePost(requestDto, postId, userId));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 수정 - 실패 (권한 없음)")
    void updatePost_Fail_NoPermission() {
        // given
        Long postId = 1L;
        User otherUser = User.builder().email("other@test.com").password("p").nickname("other").build();
        ReflectionTestUtils.setField(otherUser, "userId", 2L);
        String otherUserId = "2";
        PostRequestDto requestDto = createPostRequestDto("수정된 제목", "수정된 내용");

        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when & then
        assertThrows(NoPermissionException.class, () -> postService.updatePost(requestDto, postId, otherUserId));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost_Success() {
        // given
        Long postId = 1L;
        String userId = "1";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // when
        postService.deletePostById(postId, userId);

        // then
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 - 실패 (게시글을 찾을 수 없음)")
    void deletePost_Fail_PostNotFound() {
        // given
        Long postId = 999L;
        String userId = "1";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> postService.deletePostById(postId, userId));
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    @DisplayName("게시글 삭제 - 실패 (권한 없음)")
    void deletePost_Fail_NoPermission() {
        // given
        Long postId = 1L;
        User otherUser = User.builder().email("other@test.com").password("p").nickname("other").build();
        ReflectionTestUtils.setField(otherUser, "userId", 2L);
        String otherUserId = "2";

        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when & then
        assertThrows(NoPermissionException.class, () -> postService.deletePostById(postId, otherUserId));
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    @DisplayName("게시글 목록 조회 (페이징) - 첫 페이지")
    void getPosts_FirstPage() {
        // given
        int size = 20;
        PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "postId"));
        List<Post> posts = LongStream.rangeClosed(81, 100)
                .mapToObj(i -> {
                    Post p = Post.builder().title("t").content("c").user(user).build();
                    ReflectionTestUtils.setField(p, "postId", i);
                    return p;
                })
                .collect(Collectors.toList());
        Collections.reverse(posts);
        Slice<Post> postSlice = new SliceImpl<>(posts, pageable, true);

        when(postRepository.findAllByOrderByPostIdDesc(pageable)).thenReturn(postSlice);

        // when
        PostPageResponseDto response = postService.getPosts(null, size);

        // then
        assertThat(response.getPostList().size()).isEqualTo(size);
        assertThat(response.getCursor().getNextCursor()).isEqualTo(81L);
        assertThat(response.getCursor().isHasNext()).isTrue();
        verify(postRepository, times(1)).findAllByOrderByPostIdDesc(pageable);
    }

    @Test
    @DisplayName("게시글 목록 조회 (페이징) - 다음 페이지")
    void getPosts_NextPage() {
        // given
        int cursor = 100;
        int size = 20;
        PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "postId"));
        List<Post> posts = LongStream.rangeClosed(80, 99)
                .mapToObj(i -> Post.builder().title("t").content("c").user(user).build())
                .collect(Collectors.toList());
        Slice<Post> postSlice = new SliceImpl<>(posts, pageable, true);

        when(postRepository.findByPostIdLessThanOrderByPostIdDesc((long) cursor, pageable)).thenReturn(postSlice);

        // when
        postService.getPosts(cursor, size);

        // then
        verify(postRepository, times(1)).findByPostIdLessThanOrderByPostIdDesc((long) cursor, pageable);
    }

    @Test
    @DisplayName("게시글 목록 조회 (페이징) - 마지막 페이지")
    void getPosts_LastPage() {
        // given
        int cursor = 20;
        int size = 20;
        PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "postId"));
        List<Post> posts = LongStream.rangeClosed(1, 19)
                .mapToObj(i -> Post.builder().title("t").content("c").user(user).build())
                .collect(Collectors.toList());
        Slice<Post> postSlice = new SliceImpl<>(posts, pageable, false);

        when(postRepository.findByPostIdLessThanOrderByPostIdDesc((long) cursor, pageable)).thenReturn(postSlice);

        // when
        PostPageResponseDto response = postService.getPosts(cursor, size);

        // then
        assertThat(response.getCursor().isHasNext()).isFalse();
    }
}
