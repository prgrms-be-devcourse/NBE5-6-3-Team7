package com.grepp.diary.app.model.member;

import com.grepp.diary.app.controller.web.auth.form.SignupForm;
import com.grepp.diary.app.model.ai.AiService;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.auth.AuthService;
import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.custom.repository.CustomRepository;
import com.grepp.diary.app.model.member.dto.MemberDto;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.mail.MailTemplate;
import com.grepp.diary.infra.response.ResponseCode;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MailTemplate mailTemplate;
    private final AuthService authService;
    private final AiService aiService;
    private final CustomRepository customRepository;

    @Value("${app.domain}")
    private String domain;

    public Integer getAllMemberCount() {
        return memberRepository.countByEnabledTrue();
    }

    public void signup(HttpSession session, String token, Role role) {

        // 1. 세션에서 dto 추출 및 토큰 검증
        MemberDto dto = (MemberDto) session.getAttribute(token);

        if (dto == null) {
            throw new CommonException(ResponseCode.INVALID_TOKEN);
        }

        // 2. 중복 id 체크
        if (memberRepository.existsById(dto.getUserId())){
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }

        // 3. 비밀번호 암호화 및 역할 설정
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encodedPassword);
        dto.setRole(role);

        // 4. db 저장
        memberRepository.save(dto.toEntity());

        session.removeAttribute(token);

        // 5. 수동 로그인 처리
        UserDetails userDetails = authService.loadUserByUsername(dto.getUserId());

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // SPRING_SECURITY_CONTEXT 세션에 설정
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }

    public void sendVerificationMail(SignupForm signupForm, HttpSession session) {

        // 이메일 인증 토큰 생성 및 세션 저장
        String token = UUID.randomUUID().toString();
        MemberDto dto = signupForm.toDto();
        session.setAttribute(token, dto);

        if(memberRepository.existsById(dto.getUserId()))
            throw new CommonException(ResponseCode.BAD_REQUEST);

        mailTemplate.setTo(dto.getEmail());
        mailTemplate.setTemplatePath("/member/regist-verification");
        mailTemplate.setSubject("회원가입을 환영합니다!");
        mailTemplate.setProperties("domain", domain);
        mailTemplate.setProperties("token", token);
        mailTemplate.send();
    }

    public Member getMemberByUserId(String userId) {

        Optional<Member> result = memberRepository.findById(userId);
        if (result.isEmpty()) {
            throw new RuntimeException("존재하지 않는 회원입니다.");
        }
        return result.get();
    }

    @Transactional
    public BindingResult existsByEmail(String email, SignupForm signupForm, BindingResult bindingResult) {

        // 비밀번호, 확인 비밀번호 일치 여부
        if (!signupForm.getPassword().equals(signupForm.getRepassword())) {
            bindingResult.rejectValue("repassword", "password.mismatch", "비밀번호가 일치하지 않습니다.");
        }

        // 이메일로 회원 존재 여부 조회
        if( memberRepository.findByEmail(email).isPresent() ) {
            bindingResult.rejectValue("email", "email.exists", "등록된 이메일입니다.");
        }

        return bindingResult;
    }

    @Transactional
    public Optional<Member> findById(String userId) {
        return memberRepository.findById(userId);
    }

    @Transactional
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public boolean existsByUserIdAndEmail(String userId, String email) {
        return memberRepository.existsByUserIdAndEmail(userId, email);
    }

    @Transactional
    public Optional<Member> findByUserIdAndEmail(String userId, String email) {
        return memberRepository.findByUserIdAndEmail(userId, email);
    }

    // 비밀번호 비교를 통한 사용자 검증
    public boolean validUser(String userId, String rawPassword) {
        Member member = memberRepository.findByUserId(userId)
            .orElseThrow(() -> new CommonException(ResponseCode.MEMBER_NOT_FOUND));

        return passwordEncoder.matches(rawPassword, member.getPassword());
    }

    @Transactional
    public boolean updateEmail(String userId, String email) {
        return memberRepository.updateEmail(userId, email) > 0;
    }

    @Transactional
    public boolean updatePassword(String userId, String password) {
        return memberRepository.updatePassword(userId, passwordEncoder.encode(password)) > 0;
    }

    @Transactional
    public String getEncodedPassword(String userId, String email) {
        return memberRepository.findByUserIdAndEmail(userId, email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND))
            .getPassword();
    }

    @Transactional
    public void updatePassword(String userId, String email, String encodedPassword) {
        Optional<Member> optionalMember = memberRepository.findByUserIdAndEmail(userId, email);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setPassword(encodedPassword); // 암호화된 비밀번호 설정
        } else {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
    }

    public boolean verifyEmailCode(HttpSession session, String inputCode) {
        String sessionCode = (String) session.getAttribute("authCode");
        return sessionCode != null && sessionCode.equals(inputCode);
    }

    public String findUserIdByEmailFromSession(HttpSession session) {
        String sessionEmail = (String) session.getAttribute("authEmail");
        session.removeAttribute("authCode");
        session.removeAttribute("authEmail");
        return memberRepository.findByEmail(sessionEmail)
            .map(Member::getUserId)
            .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST, "해당 이메일로 가입된 계정이 없습니다."));
    }

    // 이메일 검증
    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    @Transactional
    public void changePassword(String userId, String email, String newPassword) {
        // 1. 비밀번호 형식 검사
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%?^&*()_+=-]).{8,}$";
        if (!newPassword.matches(passwordRegex)) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "비밀번호는 8자리 이상의 영문자, 숫자, 특수문자 조합이어야 합니다.");
        }

        // 2. 현재 비밀번호 조회
        String currentEncodedPassword = getEncodedPassword(userId, email);

        // 3. 중복 비밀번호 확인
        if (passwordEncoder.matches(newPassword, currentEncodedPassword)) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "이미 사용 중인 비밀번호입니다.");
        }

        // 4. 암호화 후 변경
        String encodedPassword = passwordEncoder.encode(newPassword);
        updatePassword(userId, email, encodedPassword);
    }

    public String findAiNameByUserId(String username) {
        // 1. userId로 Custom 조회
        Custom custom = (Custom) customRepository.findByMemberUserId(username)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "사용자 설정 정보가 없습니다."));

        // 2. Custom 객체에서 aiId 추출
        int aiId = custom.getAi().getAiId();

        // 3. aiId로 Ai 정보 조회
        Ai ai = aiService.findById(aiId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "AI 정보를 찾을 수 없습니다."));

        return ai.getName();
    }

    @Transactional
    public void withdraw(String userId) {
        // soft delete
        memberRepository.updateEnabledByUserId(userId);
    }

    public boolean isExist(String email) {
        return memberRepository.findMemberByEmail(email).isPresent();
    }
}
