package com.ktb.ktb_community.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDto {

    private String email;

    private String password;

    private String nickname;

    private String profileImageUrl;



}
