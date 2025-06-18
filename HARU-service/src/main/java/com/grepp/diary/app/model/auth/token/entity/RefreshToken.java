package com.grepp.diary.app.model.auth.token.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter @Setter
@RedisHash(value = "refreshToken") // TTL 제거: 동적으로 수동 설정할 것이므로!
public class RefreshToken {
    @Id
    private String id = UUID.randomUUID().toString();

    private String email;

    @Indexed
    private String accessTokenId;

    private String token = UUID.randomUUID().toString();

    public RefreshToken() {}

    public RefreshToken(String email, String accessTokenId) {
        this.email = email;
        this.accessTokenId = accessTokenId;
    }
}

