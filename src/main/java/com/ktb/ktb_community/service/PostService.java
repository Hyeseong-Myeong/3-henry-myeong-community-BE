package com.ktb.ktb_community.service;

import com.ktb.ktb_community.dto.*;
import com.ktb.ktb_community.entity.Post;
import com.ktb.ktb_community.entity.PostImage;
import com.ktb.ktb_community.entity.PostLike;
import com.ktb.ktb_community.entity.User;
import com.ktb.ktb_community.exception.NoPermissionException;
import com.ktb.ktb_community.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponseDto create(PostRequestDto requestDto, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("user not found"));
        List<ImageResponseDto> images = new ArrayList<>();

        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .user(user)
                .build();

        Long postId = postRepository.save(post).getPostId();
        Integer commentCount = commentRepository.countByPost_PostId(postId);

        return PostResponseDto.from(post,0, false, images, Boolean.TRUE, commentCount);
    }

    @Transactional
    public PostResponseDto getPostById(Long postId, String email) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post not found"));
        Integer likeCount = postLikeRepository.countByPost_PostId(postId);
        List<PostImage> postImageList = postImageRepository.findAllByPost_PostId(postId);
        Optional<PostLike> optionalPostLike =  postLikeRepository.findByPost_PostIdAndUser_email(postId, email);
        Boolean isLiked = false;

        List<ImageResponseDto> dtos = postImageList.stream()
                .map(PostImage::getImage)
                .map(ImageResponseDto::from)
                .toList();

        Boolean isAuthor = Boolean.FALSE;
        if(post.getUser().getEmail().equals(email)) {
            isAuthor = Boolean.TRUE;
        }
        post.incrementViewCount();
        Integer commentCount = commentRepository.countByPost_PostId(postId);


        if(optionalPostLike.isPresent()) {
            isLiked = true;
        }

        return PostResponseDto.from(post, likeCount, isLiked, dtos, isAuthor, commentCount);
    }

    public PostPageResponseDto getPosts(Integer cursor, int size){

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "postId"));


        Slice<Post> postSlice = (cursor == null)
                ? postRepository.findAllByOrderByPostIdDesc(pageable)
                : postRepository.findByPostIdLessThanOrderByPostIdDesc(Long.valueOf(cursor), pageable);

        List<PostResponseDto> postresponseDtoList = postSlice.getContent().stream()
                .map(post->{
                    Integer likeCount = postLikeRepository.countByPost_PostId(post.getPostId());
                    Integer commentCount = commentRepository.countByPost_PostId(post.getPostId());


                    return PostResponseDto.from(post, likeCount, null, null, null, commentCount);
                })
                .toList();

        Long nextCursor = null;
        if (!postresponseDtoList.isEmpty()) {
            nextCursor = postresponseDtoList.get(postresponseDtoList.size() - 1).getPostId();
        }

        CursorDto cursorDto = new CursorDto(nextCursor, postSlice.hasNext());
        return new PostPageResponseDto(postresponseDtoList, cursorDto);
    }

    @Transactional
    public PostResponseDto updatePost(PostRequestDto postRequestDto, Long postId, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("user not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post not found"));

        if(!post.getUser().getEmail().equals(user.getEmail())) {
            throw new NoPermissionException("user", "NoPermission");
        }

        post.update(postRequestDto.getTitle(), postRequestDto.getContent());

        return PostResponseDto.from(postRepository.save(post));
    }

    @Transactional
    public void deletePostById(Long postId, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("user not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post not found"));

        if(!post.getUser().getEmail().equals(user.getEmail())) {
            throw new NoPermissionException("user", "NoPermission");
        }

        postRepository.delete(post);
    }


}
