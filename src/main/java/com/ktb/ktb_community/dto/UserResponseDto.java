package com.ktb.ktb_community.dto;

import com.ktb.ktb_community.entity.User;
import lombok.Data;

@Data
public class UserResponseDto {

    private String email;

    private String nickname;

    private String profileImageUrl;

    public UserResponseDto(String email, String nickname, String profileImageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl());
    }
}
