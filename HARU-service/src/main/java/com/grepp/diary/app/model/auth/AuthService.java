package com.grepp.diary.app.model.auth;

import com.grepp.diary.app.controller.web.auth.form.SigninForm;
import com.grepp.diary.app.model.auth.domain.Principal;
import com.grepp.diary.app.model.auth.token.RefreshTokenRepository;
import com.grepp.diary.app.model.auth.token.RefreshTokenService;
import com.grepp.diary.app.model.auth.token.UserBlackListRepository;
import com.grepp.diary.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.diary.app.model.auth.token.dto.TokenDto;
import com.grepp.diary.app.model.auth.token.entity.RefreshToken;
import com.grepp.diary.app.model.mail.dto.SmtpDto;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import com.grepp.diary.infra.auth.token.JwtProvider;
import com.grepp.diary.infra.auth.token.code.GrantType;
import com.grepp.diary.infra.mail.MailApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("authService")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final Map<String, String> authCodeStorage = new HashMap<>();
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailApi mailApi;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    // ✅ 변경된 부분
    private AuthenticationManager authenticationManager;

    private final UserBlackListRepository userBlackListRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.domain}")
    private String domain;

    @Lazy
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("아이디가 존재하지 않습니다."));

        if (!member.isEnabled()) {
            throw new UsernameNotFoundException("계정이 비활성화되어 있습니다.");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));

        return Principal.createPrincipal(member, authorities);
    }

    public TokenDto verifyPasswordAndLogin(SigninForm signinForm, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(signinForm.getUserId(), signinForm.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        boolean rememberMe = "on".equals(request.getParameter("remember-me"));
        return processTokenSignin(signinForm.getUserId(), rememberMe);
    }


    public void generateAndSendCode(String email, HttpSession session, String userId) {
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));
        authCodeStorage.put(email, code);

        SmtpDto mailDto = new SmtpDto();
        mailDto.setFrom("haru");
        mailDto.setTo(List.of(email));
        mailDto.setSubject("Diary 인증번호 안내");

        Map<String, String> props = new HashMap<>();
        props.put("domain", domain);
        props.put("code", code);
        mailDto.setProperties(props);
        mailDto.setEventType("send_code");

        mailApi.sendMail("user-service", "ROLE_SERVER", mailDto);

        if (userId != null) {
            session.setAttribute("pwAuthCode", code);
            session.setAttribute("pwAuthEmail", email);
            session.setAttribute("pwAuthUserId", userId);
        } else {
            session.setAttribute("idAuthCode", code);
            session.setAttribute("idAuthEmail", email);
        }
    }

    public TokenDto processTokenSignin(String username, boolean rememberMe) {
        userBlackListRepository.deleteById(username);
        AccessTokenDto dto = jwtProvider.generateAccessToken(username);
        long rtExpiration = rememberMe ? 60L * 60 * 24 * 30 : 60L * 60 * 24;

        RefreshToken refreshToken = new RefreshToken(username, dto.getId());
        refreshTokenService.saveWithExpiration(refreshToken, rtExpiration);

        return TokenDto.builder()
            .accessToken(dto.getToken())
            .refreshToken(refreshToken.getToken())
            .atExpiresIn(jwtProvider.getAtExpiration())
            .rtExpiresIn(rtExpiration)
            .grantType(GrantType.BEARER)
            .build();
    }


    public TokenDto generateTokenAfterSignup(java.security.Principal principal) {
        String username = principal.getName(); // userId

        // 1. 블랙리스트 삭제
        userBlackListRepository.deleteById(username);

        // 2. AccessToken 생성
        AccessTokenDto accessTokenDto = jwtProvider.generateAccessToken(username);

        // 3. RefreshToken 생성 및 저장 (기본 30일)
        long rtExpiration = 60L * 60 * 24 * 30;
        RefreshToken refreshToken = new RefreshToken(username, accessTokenDto.getId());
        refreshTokenService.saveWithExpiration(refreshToken, rtExpiration);

        // 4. TokenDto 생성 및 반환
        return TokenDto.builder()
            .accessToken(accessTokenDto.getToken())
            .refreshToken(refreshToken.getToken())
            .atExpiresIn(jwtProvider.getAtExpiration())
            .rtExpiresIn(rtExpiration)
            .grantType(GrantType.BEARER)
            .build();
    }

}