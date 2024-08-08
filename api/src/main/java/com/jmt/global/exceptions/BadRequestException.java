package com.jmt.global.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class BadRequestException extends CommonException {

    public BadRequestException(String message) { // 일반 에러 메세지
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(Map<String, List<String>> errorMessages) { // 커맨드 객체 검증
        super("", HttpStatus.BAD_REQUEST);
        setErrorMessages(errorMessages);
    }
}
