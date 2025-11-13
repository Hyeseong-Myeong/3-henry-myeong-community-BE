package com.ktb.ktb_community.exception.handler;

import com.ktb.ktb_community.common.advice.ErrorResponse;
import com.ktb.ktb_community.exception.DuplicatedException;
import com.ktb.ktb_community.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ktb.ktv_community.controller.user")
public class UserExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotfound(NotFoundException e) {
        ErrorResponse response = new ErrorResponse("USER_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<ErrorResponse> duplicated(DuplicatedException e) {
        ErrorResponse response = new ErrorResponse("USER_CONFLICT", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

}
