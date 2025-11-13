package com.ktb.ktb_community.common.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

//null 필드는 응답에 포함하지 않음
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ErrorResponse{

    private final String errorCode;
    private final String message;
    private final Map<String, String> errors;

    //@Valid 검증 에러를 위한 생성자
    public ErrorResponse(String errorCode, Map<String, String> errors) {
        this.errorCode = errorCode;
        this.message = "입력 값이 유효하지 않습니다."; // 공통 메시지
        this.errors = errors;
    }

    //비즈니스 예외(단일 메시지)를 위한 생성자
    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.errors = null; // 필드 에러가 아님
    }
}
