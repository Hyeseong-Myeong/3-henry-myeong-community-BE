package com.ktb.ktb_community.repository;


import com.ktb.ktb_community.common.Security.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Long userId(Long userId);
}