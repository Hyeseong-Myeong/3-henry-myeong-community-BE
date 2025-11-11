package com.ktb.ktb_community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostRequestDto {

    @NotBlank
    @Size(max = 26)
    private String title;

    @NotBlank
    private String content;
}
