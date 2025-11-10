package com.ktb.ktb_community.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDto {

    //공백 안됨
    //이메일 형식
    private String email;

    //공백 안됨
    // 글자수 범위 제한 및 필수 포함 문자
    private String password;

    //공백 안됨
    //중복 검증은 비즈니스 로직
    private String nickname;

    //공백 가능
    private String profileImageUrl;



}
