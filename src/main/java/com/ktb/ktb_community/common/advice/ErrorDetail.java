package com.ktb.ktb_community.common.advice;

import lombok.Data;

@Data
public class ErrorDetail {
    private String field;
    private String reason;

    public ErrorDetail(String field, String reason) {
        this.field = field;
        this.reason = reason;
    }
    // Getters...
}