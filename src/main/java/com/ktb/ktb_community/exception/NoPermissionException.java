package com.ktb.ktb_community.exception;

public class NoPermissionException extends RuntimeException {
    private final String field;
    private final String reason;

    public NoPermissionException(String field, String reason) {
        super(reason); // 부모 클래스에 메시지 전달
        this.field = field;
        this.reason = reason;
    }
}
