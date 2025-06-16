package com.grepp.diary.infra.auth.oauth2;

import com.grepp.diary.app.model.auth.AuthService;
import com.grepp.diary.app.model.auth.token.dto.TokenDto;
import com.grepp.diary.infra.auth.oauth2.user.OAuth2UserInfo;
import com.grepp.diary.infra.auth.token.TokenCookieFactory;
import com.grepp.diary.infra.auth.token.code.TokenType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final AuthService authService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        OAuth2UserInfo userInfo = OAuth2UserInfo.create(request.getRequestURI(), user);
        TokenDto dto = authService.processTokenSignin(userInfo.getName());
        
        ResponseCookie accessTokenCookie = TokenCookieFactory.create(TokenType.ACCESS_TOKEN.name(),
            dto.getAccessToken(), dto.getAtExpiresIn());
        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(TokenType.REFRESH_TOKEN.name(),
            dto.getRefreshToken(), dto.getRtExpiresIn());
        
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        getRedirectStrategy().sendRedirect(request,response,"/");
    }
}
