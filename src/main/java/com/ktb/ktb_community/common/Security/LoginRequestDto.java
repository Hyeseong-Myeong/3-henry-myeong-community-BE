package com.ktb.ktb_community.common.Security;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
