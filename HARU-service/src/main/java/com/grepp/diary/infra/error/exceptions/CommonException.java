package com.grepp.diary.infra.error.exceptions;

import com.grepp.diary.infra.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonException extends RuntimeException {
    
    private final ResponseCode code;
    
    public CommonException(ResponseCode code) {
        super(code.message());
        this.code = code;
    }
    
    public CommonException(ResponseCode code, Exception e) {
        super(code.message(), e);
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public CommonException(ResponseCode code, String message) {
        super(message);
        this.code = code;
    }

    public ResponseCode code() {
        return code;
    }
}
