package com.ktb.ktb_community.dto;

import com.ktb.ktb_community.entity.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageResponseDto {

    private String url;

    public ImageResponseDto(String url) {
        this.url = url;
    }

    public static ImageResponseDto from(Image image) {
        return new ImageResponseDto(
                image.getImageUrl()
        );
    }
}
