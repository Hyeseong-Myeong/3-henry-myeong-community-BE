package com.ktb.ktb_community.common.Security;

import lombok.Data;

@Data
public class LoginSuccessResponseDto {
    private String accessToken;

    public LoginSuccessResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}