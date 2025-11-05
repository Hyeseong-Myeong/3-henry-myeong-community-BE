package com.ktb.ktb_community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorDto {

    private Long nextCursor;
    private boolean hasNext;
}