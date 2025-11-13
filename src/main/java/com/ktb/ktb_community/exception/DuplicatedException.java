package com.ktb.ktb_community.exception;

import lombok.Getter;

@Getter
public class DuplicatedException extends BusinessException {

    private final String field;

    public DuplicatedException(String field, String message) {
        super(message); // 부모 클래스에 메시지 전달
        this.field = field;
    }
}
