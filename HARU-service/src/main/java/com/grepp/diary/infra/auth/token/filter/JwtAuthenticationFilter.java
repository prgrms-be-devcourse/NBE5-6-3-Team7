package com.grepp.diary.infra.auth.token.filter;

import com.grepp.diary.app.model.auth.token.RefreshTokenService;
import com.grepp.diary.app.model.auth.token.UserBlackListRepository;
import com.grepp.diary.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.diary.app.model.auth.token.entity.RefreshToken;
import com.grepp.diary.infra.auth.token.JwtProvider;
import com.grepp.diary.infra.auth.token.TokenCookieFactory;
import com.grepp.diary.infra.auth.token.code.TokenType;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter implements Ordered {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1; // 순서 지정
    }

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> excludePath = new ArrayList<>();
        excludePath.addAll(List.of("/error", "/favicon.ico", "/css", "/img","/js","/download"));
        excludePath.addAll(List.of("/member/signup", "/member/signin"));
        String path = request.getRequestURI();
        return excludePath.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        log.info("[JwtFilter] 요청 URI: {}", request.getRequestURI());

        String requestAccessToken = jwtProvider.resolveToken(request, TokenType.ACCESS_TOKEN);
        log.info("[JwtFilter] AccessToken from Cookie: {}", requestAccessToken);

        if (requestAccessToken == null) {
            log.warn("[JwtFilter] AccessToken 없음, 필터Authentication authentication = jwtProvider.genreateAuthentication(newAccessToken.getToken());\n"
                + "SecurityContextHolder.getContext().setAuthentication(authentication); 통과");
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims;
        try {
            claims = jwtProvider.parseClaim(requestAccessToken);
        } catch (Exception e) {
            log.error("[JwtFilter] 토큰 Claim 파싱 실패: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtProvider.validateToken(requestAccessToken)) {
                Authentication authentication = jwtProvider.genreateAuthentication(requestAccessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("[JwtFilter] accessToken 유효함, 사용자 인증 완료: {}", authentication.getName());

                // ✅ 세션에도 인증 정보 저장 (재요청 시 SecurityContextHolder 복원 가능하도록)
                HttpSession session = request.getSession(true);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            } else {
                log.warn("[JwtFilter] accessToken 유효하지 않음");
            }
        } catch (ExpiredJwtException ex) {
            log.warn("[JwtFilter] accessToken 만료됨, 재발급 시도");

            AccessTokenDto newAccessToken = renewingAccessToken(requestAccessToken, request, response);
            if (newAccessToken == null) {
                log.error("[JwtFilter] accessToken 재발급 실패 - refreshToken 검증 실패 또는 없음");
                filterChain.doFilter(request, response);
                return;
            }

            RefreshToken newRefreshToken = renewingRefreshToken(claims.getId(), newAccessToken.getId());

            // ✅ 여기서 인증 객체 재설정!
            Authentication authentication = jwtProvider.genreateAuthentication(newAccessToken.getToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[JwtFilter] 재발급된 accessToken으로 인증 객체 설정 완료: {}", authentication.getName());


            HttpSession session = request.getSession(true);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);



            responseToken(response, newAccessToken, newRefreshToken);
            log.info("[JwtFilter] accessToken 재발급 완료 - userId: {}", claims.getSubject());
        }

        filterChain.doFilter(request, response);
    }

    private void responseToken(HttpServletResponse response, AccessTokenDto newAccessToken,
        RefreshToken newRefreshToken) {

        // 쿠키 생성
        ResponseCookie accessTokenCookie = TokenCookieFactory.create(
            TokenType.ACCESS_TOKEN.name(),
            newAccessToken.getToken(),
            jwtProvider.getRtExpiration()
        );

        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(
            TokenType.REFRESH_TOKEN.name(),
            newRefreshToken.getToken(),
            jwtProvider.getRtExpiration()
        );

        // 디버깅 로그 추가
        log.info("[JwtFilter] Set-Cookie: ACCESS_TOKEN={}, MaxAge={}, Raw={}",
            newAccessToken.getToken(),
            3000000L,
            accessTokenCookie.toString());

        log.info("[JwtFilter] Set-Cookie: REFRESH_TOKEN={}, MaxAge={}, Raw={}",
            newRefreshToken.getToken(),
            jwtProvider.getAtExpiration(),
            refreshTokenCookie.toString());

        // 쿠키를 응답 헤더에 추가
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

    private RefreshToken renewingRefreshToken(String id, String newTokenId) {
        return refreshTokenService.renewingToken(id, newTokenId);
    }
    
    private AccessTokenDto renewingAccessToken(String requestAccessToken, HttpServletRequest request,
        HttpServletResponse response) {
        Authentication authentication = jwtProvider.genreateAuthentication(requestAccessToken);
        String refreshToken = jwtProvider.resolveToken(request, TokenType.REFRESH_TOKEN);
        Claims claims = jwtProvider.parseClaim(requestAccessToken);
        
        RefreshToken storedRefreshToken = refreshTokenService.findByAccessTokenId(claims.getId());
        
        if(storedRefreshToken == null) {
            return null;
        }
        
        if (!storedRefreshToken.getToken().equals(refreshToken)) {
//            userBlackListRepository.save(new UserBlackList(authentication.getName()));
//
//            refreshTokenService.deleteByRefreshToken(refreshToken);
//            refreshTokenService.deleteByAccessTokenId(claims.getId());
//            ResponseCookie expireTokenCookie = TokenCookieFactory.createExpiredToken(TokenType.ACCESS_TOKEN);
//            ResponseCookie expiredRefCookie = TokenCookieFactory.createExpiredToken(TokenType.REFRESH_TOKEN);
//
//            response.setHeader("Set-Cookie", expireTokenCookie.toString());
//            response.setHeader("Set-Cookie", expiredRefCookie.toString());
            throw new CommonException(ResponseCode.SECURITY_INCIDENT);
        }
        
        return generateAccessToken(authentication);
    }
    
    private AccessTokenDto generateAccessToken(Authentication authentication) {
        AccessTokenDto newAccessToken = jwtProvider.generateAccessToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return newAccessToken;
    }
}
