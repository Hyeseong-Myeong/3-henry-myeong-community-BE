package com.ktb.ktb_community.repository;

import com.ktb.ktb_community.entity.PostImage;
import com.ktb.ktb_community.entity.PostImageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, PostImageId> {

    List<PostImage> findAllByPost_PostId(Long postId);
}
