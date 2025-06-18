package com.grepp.diary.infra.auth.token;

import static org.springframework.http.ResponseCookie.from;

import com.grepp.diary.infra.auth.token.code.TokenType;
import org.springframework.http.ResponseCookie;

public class TokenCookieFactory {
    public static ResponseCookie create(String name, String value, Long expires){
        return from(name, value)
            .httpOnly(true)
            .maxAge(expires)
            .secure(true)
            .sameSite("Lax")
            .path("/")
            .build();
    }

    public static ResponseCookie createExpiredToken(TokenType tokenType) {
        return from(tokenType.name(), "")
            .httpOnly(true)
            .maxAge(0)
            .secure(true)
            .sameSite("Lax")
            .path("/")
            .build();
    }
}
