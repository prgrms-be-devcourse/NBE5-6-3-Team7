package com.grepp.diary.app.model.auth.token;

import com.grepp.diary.app.model.auth.token.entity.RefreshToken;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void deleteByAccessTokenId(String id) {
        Optional<RefreshToken> optional = refreshTokenRepository.findByAccessTokenId(id);
        optional.ifPresent(e -> refreshTokenRepository.deleteById(e.getId()));
    }

    public void deleteByRefreshToken(String token) {
        Optional<RefreshToken> optional = refreshTokenRepository.findByToken(token);
        optional.ifPresent(e -> refreshTokenRepository.deleteById(e.getId()));
    }

    public void delete(String id){
        refreshTokenRepository.deleteById(id);
    }
    
    public RefreshToken renewingToken(String id, String newTokenId) {
        RefreshToken refreshToken = findByAccessTokenId(id);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setAccessTokenId(newTokenId);
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
    
    public RefreshToken findByAccessTokenId(String id){
        return refreshTokenRepository.findByAccessTokenId(id).orElse(null);
    }

    public void saveWithExpiration(RefreshToken refreshToken, long rtExpiration) {
        // 저장
        refreshTokenRepository.save(refreshToken);

        // Redis 키를 직접 설정 (RedisHash key prefix는 "refreshToken:" + id)
        String key = "refreshToken:" + refreshToken.getId();

        // TTL 적용
        redisTemplate.expire(key, Duration.ofSeconds(rtExpiration));
    }
}
