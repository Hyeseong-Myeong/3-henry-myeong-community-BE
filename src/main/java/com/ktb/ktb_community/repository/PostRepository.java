package com.ktb.ktb_community.repository;

import com.ktb.ktb_community.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Slice<Post> findAllByOrderByPostIdDesc(Pageable pageable);

    Slice<Post> findByPostIdLessThanOrderByPostIdDesc(Long cursor, Pageable pageable);

}
