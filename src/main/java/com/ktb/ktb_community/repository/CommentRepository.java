package com.ktb.ktb_community.repository;

import com.ktb.ktb_community.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Slice<Comment> findByPost_PostIdOrderByCommentIdAsc(Long postId, Pageable pageable);

    Slice<Comment> findByPost_PostIdAndCommentIdGreaterThanOrderByCommentIdAsc(Long postId, Long cursor, Pageable pageable);

    Integer countByPost_PostId(Long postId);
}
