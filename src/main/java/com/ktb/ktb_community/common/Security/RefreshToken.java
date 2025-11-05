package com.ktb.ktb_community.common.Security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 1209600) //14일
@NoArgsConstructor
public class RefreshToken {

    @Id
    private String token;

    private Long userId;

    public RefreshToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}