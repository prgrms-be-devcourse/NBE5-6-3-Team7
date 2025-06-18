package com.grepp.diary.app.model.auth.token.dto;

import com.grepp.diary.infra.auth.token.code.GrantType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private GrantType grantType;
    private Long atExpiresIn;
    private Long rtExpiresIn;
}

