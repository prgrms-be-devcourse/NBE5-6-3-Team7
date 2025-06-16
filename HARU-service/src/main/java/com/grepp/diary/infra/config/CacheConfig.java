package com.grepp.diary.infra.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(24)) // 캐시 만료 24시간
            .disableCachingNullValues() // null 값은 캐시하지 않음
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer())) // key 를 String 으로 직렬화
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer())); // value 를 String 으로 직렬화
    }
}
