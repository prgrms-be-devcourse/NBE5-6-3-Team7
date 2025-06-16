package com.grepp.diary.app.model.auth;

import com.grepp.diary.app.controller.web.auth.form.SigninForm;
import com.grepp.diary.app.model.auth.domain.Principal;
import com.grepp.diary.app.model.auth.token.RefreshTokenRepository;
import com.grepp.diary.app.model.auth.token.UserBlackListRepository;
import com.grepp.diary.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.diary.app.model.auth.token.dto.TokenDto;
import com.grepp.diary.app.model.auth.token.entity.RefreshToken;
import com.grepp.diary.app.model.member.dto.SmtpDto;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("authService")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final Map<String, String> authCodeStorage = new HashMap<>(); // 인증번호 저장용
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailApi mailApi;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserBlackListRepository userBlackListRepository;

    public UserDetails loadUserByUsername(String username) {

        Member member = memberRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("아이디가 존재하지 않습니다."));

        if(!member.isEnabled()) {
            throw new UsernameNotFoundException("계정이 비활성화되어 있습니다.");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));

        // 스프링시큐리티는 기본적으로 권한 앞에 ROLE_ 이 있음을 가정
        // hasRole("ADMIN") =>  ROLE_ADMIN 권한이 있는 지 확인.
        return Principal.createPrincipal(member, authorities);
    }

    public TokenDto verifyPasswordAndLogin(SigninForm signinForm, HttpServletRequest request) {

        // 1. 사용자 데이터 조회
        UserDetails userDetails = loadUserByUsername(signinForm.getUserId());

        // 2. 비밀번호 체크
        if (!passwordEncoder.matches(signinForm.getPassword(), userDetails.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        // 3. 인증 설정
//        UsernamePasswordAuthenticationToken authToken =
//            new UsernamePasswordAuthenticationToken(userDetails, null,
//                userDetails.getAuthorities());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(signinForm.getUserId(), signinForm.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        request.getSession()
            .setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return processTokenSignin(signinForm.getUserId());
    }


    public void generateAndSendCode(String email, HttpSession session, String userId) {
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));
        authCodeStorage.put(email, code);

        SmtpDto mailDto = new SmtpDto();
        mailDto.setFrom("haru");
        mailDto.setTo(List.of(email));
        String subject = "Diary 인증번호 안내";
        mailDto.setSubject(subject);
        Map<String, String> props = new HashMap<>();
        props.put("code", code);

        mailDto.setProperties(props);
        mailDto.setEventType("send_code");

        mailApi.sendMail(
            "user-service",
            "ROLE_SERVER",
            mailDto
        );

        session.setAttribute("authCode", code);
        session.setAttribute("authEmail", email);
        if (userId != null) {
            session.setAttribute("authUserId", userId);
        }
    }

    public TokenDto processTokenSignin(String username) {
        // 블랙리스트에서 제거
        userBlackListRepository.deleteById(username);

        AccessTokenDto dto = jwtProvider.generateAccessToken(username);
        RefreshToken refreshToken = new RefreshToken(username, dto.getId());
        refreshTokenRepository.save(refreshToken);

        return TokenDto.builder()
            .accessToken(dto.getToken())
            .refreshToken(refreshToken.getToken())
            .atExpiresIn(jwtProvider.getAtExpiration())
            .rtExpiresIn(jwtProvider.getRtExpiration())
            .grantType(GrantType.BEARER)
            .build();
    }
}
