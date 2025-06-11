package com.grepp.diary.app.model.auth;

import com.grepp.diary.app.controller.web.auth.form.SigninForm;
import com.grepp.diary.app.model.auth.domain.Principal;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import com.grepp.diary.infra.mail.MailTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final MailTemplate mailTemplate;
    private final Map<String, String> authCodeStorage = new HashMap<>(); // 인증번호 저장용
    private final JavaMailSender javaMailSender;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final @Lazy RememberMeServices rememberMeServices;

    @Value("${spring.mail.username}")
    private String from;

    @Override
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

    public void verifyPasswordAndLogin(SigninForm signinForm, HttpServletRequest request, HttpServletResponse response) {

        // 1. 사용자 데이터 조회
        UserDetails userDetails = loadUserByUsername(signinForm.getUserId());

        // 2. 비밀번호 체크
        if (!passwordEncoder.matches(signinForm.getPassword(), userDetails.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        // 3. 강제 인증 설정
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        request.getSession()
            .setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        rememberMeServices.loginSuccess(request, response, authToken);
    }


    public void generateAndSendCode(String email, HttpSession session, String userId) {
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));
        authCodeStorage.put(email, code);

        String subject = "Diary 인증번호 안내";
        String text = "안녕하세요.\n\n" +
            "요청하신 인증번호는 아래와 같습니다.\n\n" +
            "인증번호는 [" + code + "] 입니다.\n\n" +
            "감사합니다.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);

        session.setAttribute("authCode", code);
        session.setAttribute("authEmail", email);
        if (userId != null) {
            session.setAttribute("authUserId", userId);
        }
    }


}
