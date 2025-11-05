package com.ktb.ktb_community.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class S3ResponseDto {

    private String preSignedUrl;

    private String key;

    @Builder
    public S3ResponseDto(String preSignedUrl, String key) {
        this.preSignedUrl = preSignedUrl;
        this.key = key;
    }
}
