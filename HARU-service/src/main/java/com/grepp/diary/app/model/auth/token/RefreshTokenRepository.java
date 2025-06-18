package com.grepp.diary.app.model.auth.token;

import com.grepp.diary.app.model.auth.token.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByAccessTokenId(String id);
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
}
