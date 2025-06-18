package com.grepp.diary.infra.auth.token.filter;

import com.grepp.diary.app.model.auth.token.RefreshTokenService;
import com.grepp.diary.infra.auth.token.JwtProvider;
import com.grepp.diary.infra.auth.token.TokenCookieFactory;
import com.grepp.diary.infra.auth.token.code.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Order(0)
public class LogoutFilter extends OncePerRequestFilter {
    
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (!path.equals("/auth/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtProvider.resolveToken(request, TokenType.ACCESS_TOKEN);
        if (accessToken == null) {
            response.sendRedirect("/");
            return;
        }

        Claims claims = jwtProvider.parseClaim(accessToken);

        // ✅ DB 토큰 삭제
        refreshTokenService.deleteByAccessTokenId(claims.getId());

        // ✅ 쿠키 만료
        ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(TokenType.ACCESS_TOKEN);
        ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(TokenType.REFRESH_TOKEN);
        ResponseCookie expiredSessionId = TokenCookieFactory.createExpiredToken(TokenType.AUTH_SERVER_SESSION_ID);

        response.addHeader("Set-Cookie", expiredAccessToken.toString());
        response.addHeader("Set-Cookie", expiredRefreshToken.toString());
        response.addHeader("Set-Cookie", expiredSessionId.toString());

        // ✅ 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // ✅ SecurityContext 제거
        SecurityContextHolder.clearContext();

        // ✅ 리다이렉트 후 종료
        response.sendRedirect("/");
    }

}
