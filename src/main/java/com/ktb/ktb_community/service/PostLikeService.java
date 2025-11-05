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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostLikeResponseDto createLike (Long postId, String userEmail) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("post", "not found"));
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("user", "not found"));

        Optional<PostLike> optionalPostLike = postLikeRepository.findByPost_PostIdAndUser_email(postId, userEmail);

        if(optionalPostLike.isPresent()) {
            throw new DuplicatedException("postLike", "already liked");
        }

        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(postLike);

        Integer count =  postLikeRepository.countByPost_PostId(postId);

        return new PostLikeResponseDto(count, true);
    }

    @Transactional
    public PostLikeResponseDto deleteLike (Long postId, String userEmail) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("post", "not found"));
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("user", "not found"));

        Optional<PostLike> optionalPostLike = postLikeRepository.findByPost_PostIdAndUser_email(postId, userEmail);

        if(optionalPostLike.isEmpty()){
            throw new NotFoundException("postLike", "not found");
        }

        postLikeRepository.delete(optionalPostLike.get());

        Integer count =  postLikeRepository.countByPost_PostId(postId);

        return new PostLikeResponseDto(count, false);
    }
}
