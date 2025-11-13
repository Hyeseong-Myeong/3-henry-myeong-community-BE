package com.ktb.ktb_community.exception;

//비즈니스 로직에서 발생하는 모든 예외는 해당 클래스를 상속
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
