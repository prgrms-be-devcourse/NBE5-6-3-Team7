package com.grepp.diary.infra.auth.token;

import com.grepp.diary.app.model.auth.domain.Principal;
import com.grepp.diary.app.model.auth.token.RefreshTokenRepository;
import com.grepp.diary.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.diary.infra.auth.UserDetailsServiceImpl;
import com.grepp.diary.infra.auth.token.code.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    
    private RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDetailsServiceImpl userDetailsService;
    
    @Value("${jwt.secrete}")
    private String key;
    
    @Getter
    @Value("${jwt.access-expiration}")
    private long atExpiration;
    
    @Getter
    @Value("${jwt.refresh-expiration}")
    private long rtExpiration;
    
    private SecretKey secretKey;
    
    public SecretKey getSecretKey(){
        if(secretKey == null){
            String base64Key = Base64.getEncoder().encodeToString(key.getBytes());
            secretKey = Keys.hmacShaKeyFor(base64Key.getBytes(StandardCharsets.UTF_8));
        }
        return secretKey;
    }
    
    public AccessTokenDto generateAccessToken(Authentication authentication){
        return generateAccessToken(authentication.getName());
    }
    
    public AccessTokenDto generateAccessToken(String username){
        String id = UUID.randomUUID().toString();
        long now = new Date().getTime();
        Date atExpiresIn = new Date(now + atExpiration);
        String accessToken = Jwts.builder()
                                 .subject(username)
                                 .id(id)
                                 .expiration(atExpiresIn)
                                 .signWith(getSecretKey())
                                 .compact();
        
        return AccessTokenDto.builder()
                   .id(id)
                   .token(accessToken)
                   .expiresIn(atExpiresIn.getTime())
                   .build();
    }

    public Authentication genreateAuthentication(String accessToken) {
        Claims claims = parseClaim(accessToken);

        // 캐시에 저장된 값이 SimpleGrantedAuthority가 아니라면 역직렬화 에러 발생
        // 따라서 캐시나 직접 DB에서 권한을 조회해서 SimpleGrantedAuthority 리스트를 구성해야 함
        List<SimpleGrantedAuthority> authorities = userDetailsService.findAuthorities(claims.getSubject());

        Principal principal = new Principal(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }






    public Claims parseClaim(String accessToken) {
        try{
            return Jwts.parser().verifyWith(getSecretKey()).build()
                       .parseSignedClaims(accessToken).getPayload();
        }catch (ExpiredJwtException ex){
            return ex.getClaims();
        }
    }
    
    
    public boolean validateToken(String requestAccessToken) {
        try{
            Jwts.parser().verifyWith(getSecretKey()).build().parse(requestAccessToken);
            return true;
        }catch(SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e){
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public String resolveToken(HttpServletRequest request, TokenType tokenType) {
        // 1. Authorization 헤더
        String headerToken = request.getHeader("Authorization");
        if (headerToken != null && headerToken.toLowerCase().startsWith("bearer ")) {
            return headerToken.substring(7).trim();
        }

        // 2. 쿠키 확인
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(tokenType.name())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }


}
