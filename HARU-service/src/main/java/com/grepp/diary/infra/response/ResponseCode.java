package com.grepp.diary.infra.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    OK("0000", HttpStatus.OK, "정상적으로 완료되었습니다."),
    BAD_REQUEST("4000", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED("4001", HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    INVALID_TOKEN("4002", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    NOT_FOUND("4004", HttpStatus.NOT_FOUND, "비밀번호를 찾을 수 없습니다."),
    DIARY_ALREADY_EXISTS("4005", HttpStatus.CONFLICT, "해당 날짜에 작성된 일기가 이미 존재합니다."),
    MEMBER_NOT_FOUND("4010", HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    INVALID_EMAIL_FORMAT("4011", HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),
    INTERNAL_SERVER_ERROR("5000", HttpStatus.INTERNAL_SERVER_ERROR, "서버에러 입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
    
    ResponseCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
    
    public String code() {
        return code;
    }
    
    public HttpStatus status() {
        return status;
    }
    
    public String message() {
        return message;
    }
}
