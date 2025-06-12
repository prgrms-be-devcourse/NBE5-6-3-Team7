package com.grepp.diary.infra.feign.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FeignCommonException extends RuntimeException {

    private String code;
    private String message;
    private HttpStatus status;

    public FeignCommonException(){
        super();
    };

    public FeignCommonException(Throwable cause) {
        super(cause);
    }

    public FeignCommonException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public FeignCommonException(Throwable cause, String code, String message, HttpStatus status) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
