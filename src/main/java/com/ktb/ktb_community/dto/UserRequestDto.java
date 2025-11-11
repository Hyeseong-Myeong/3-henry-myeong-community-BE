package com.ktb.ktb_community.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "email 은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "password 는 필수 입력 값입니다.")
    @Size(min = 8,max = 20, message = "비밀번호는 최소 8자 이상, 20자 이하이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
    )
    private String password;

    @NotBlank
    @Size(max = 10, message = "닉네임은 최대 10글자까지 입력 가능합니다.")
    @Pattern(
            regexp = "^\\S*$",
            message = "닉네임에 띄어쓰기(공백)를 사용할 수 없습니다."
    )
    //중복 검증은 비즈니스 로직
    private String nickname;

    //공백 가능
    private String profileImageUrl;



}
