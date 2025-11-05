package com.ktb.ktb_community.common.advice;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private String message;
    private List<ErrorDetail> errors;

    public ErrorResponse(String message, List<ErrorDetail> errors) {
        this.message = message;
        this.errors = errors;
    }
}
