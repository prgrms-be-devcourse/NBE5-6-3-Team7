package com.grepp.diary.infra.auth.oauth2;

import com.grepp.diary.app.model.auth.AuthService;
import com.grepp.diary.app.model.auth.token.RefreshTokenService;
import com.grepp.diary.infra.auth.token.JwtProvider;
import com.grepp.diary.infra.auth.token.TokenCookieFactory;
import com.grepp.diary.infra.auth.token.code.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        
        String requestAccessToken = jwtProvider.resolveToken(request, TokenType.ACCESS_TOKEN);
        if (requestAccessToken == null) {
            return;
        }
        
        Claims claims = jwtProvider.parseClaim(requestAccessToken);
        refreshTokenService.deleteByAccessTokenId(claims.getId());
        
        ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(TokenType.ACCESS_TOKEN);
        ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(TokenType.REFRESH_TOKEN);
        response.addHeader("Set-Cookie", expiredAccessToken.toString());
        response.addHeader("Set-Cookie", expiredRefreshToken.toString());
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
