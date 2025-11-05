package com.ktb.ktb_community.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class PostImageId implements Serializable {

    private Long post;
    private Long image;
}
