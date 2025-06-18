package com.grepp.diary.app.controller.web.auth;

import com.grepp.diary.app.controller.web.auth.form.SigninForm;
import com.grepp.diary.app.controller.web.auth.form.SignupForm;
import com.grepp.diary.app.model.auth.AuthService;
import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.auth.token.dto.TokenDto;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.infra.auth.token.JwtProvider;
import com.grepp.diary.infra.auth.token.TokenCookieFactory;
import com.grepp.diary.infra.auth.token.code.TokenType;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final AuthService authService;
    private final CustomService customService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("signinForm") SigninForm signinForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request, HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.signinForm", bindingResult);
            redirectAttributes.addFlashAttribute("signinForm", signinForm);
            return "redirect:/";
        }

        try {
            TokenDto dto = authService.verifyPasswordAndLogin(signinForm, request);
            ResponseCookie accessTokenCookie = TokenCookieFactory.create(TokenType.ACCESS_TOKEN.name(),
                dto.getAccessToken(), dto.getAtExpiresIn());
            ResponseCookie refreshTokenCookie = TokenCookieFactory.create(TokenType.REFRESH_TOKEN.name(),
                dto.getRefreshToken(), dto.getRtExpiresIn());

            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

            return "redirect:/app";
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            // 로그인 실패 시
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("signinForm", signinForm);
            return "redirect:/";
        }
    }

    // 회원가입 페이지 반환
    @GetMapping("/regist")
    public String register(Model model) {
        if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupForm());
        }
        return "/member/regist";
    }


    // 회원가입 요청 → 이메일 인증 메일 전송
    @PostMapping("/regist")
    public String register(@Valid @ModelAttribute("signupForm") SignupForm signupForm,
        BindingResult bindingResult,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        bindingResult = memberService.existsByEmail(signupForm.getEmail(), signupForm, bindingResult);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.signupForm", bindingResult);
            redirectAttributes.addFlashAttribute("signupForm", signupForm);
            return "redirect:/auth/regist";
        }

        try {
            memberService.sendVerificationMail(signupForm, session);
            redirectAttributes.addFlashAttribute("message", "인증 메일을 전송했습니다. 이메일을 확인해 주세요.");
            return "redirect:/auth/regist-mail";
        } catch (CommonException e) {
            if (e.code() == ResponseCode.BAD_REQUEST) {
                bindingResult.rejectValue("userId", "user.exists", "이미 사용 중인 아이디입니다.");
                redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.signupForm", bindingResult);
                redirectAttributes.addFlashAttribute("signupForm", signupForm);
                return "redirect:/auth/regist";
            }

            // 기타 예외 처리
            redirectAttributes.addFlashAttribute("error", "메일 전송 중 오류가 발생했습니다.");
            return "redirect:/auth/regist";
        }
    }

    @GetMapping("/regist-mail")
    public String showRegistMailPage() {
        return "member/regist-mail";
    }

    // 이메일 인증 -> 회원가입(db 등록)
    @GetMapping("regist/{token}")
    public String verifiedRegist(
        @PathVariable
        String token,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        try {
            memberService.signup(session, token, Role.ROLE_USER); // 일반 사용자로 회원가입
            return "redirect:/app";
        } catch (CommonException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.code().message());
            return "redirect:/app";
        }
    }


    // 아이디 찾기 페이지 반환
    @GetMapping("/find-idpw")
    public String findIdPage(Model model) {

        return "/member/find-idpw";
    }

    // 아이디 + 비번 합쳐서 (이메일 유효성)검사 + 인증코드 전송
    @PostMapping("/auth-idpw")
    public String sendAuthCodeForId(@RequestParam String email, Model model,
        @RequestParam(required = false) String userId,
        HttpSession session) {
        if(userId != null) {
            if (!memberService.existsByUserIdAndEmail(userId, email)) {
                model.addAttribute("error", "일치하는 계정이 없습니다.");
                model.addAttribute("type", "pw");
                return "member/find-idpw";
            }
        } else {
            if (!memberService.isValidEmail(email)) {
                model.addAttribute("type", "pw");
                model.addAttribute("error", "유효한 이메일 형식이 아닙니다.");
                return "member/find-idpw";
            }
        }

        try {
            // 아이디 + 비번 검사 2단계 : 인증 코드 전송 (세션 처리 포함)
            if (userId != null) {
                // 비번 찾기
                authService.generateAndSendCode(email, session, userId);
                model.addAttribute("userId", userId);
                model.addAttribute("type", "pw");
            }else {
                // 아이디찾기
                authService.generateAndSendCode(email, session, null);
            }
            model.addAttribute("email", email);
            model.addAttribute("step", "verify");
            model.addAttribute("message", "인증번호가 전송되었습니다.");
            return "member/find-idpw";
        } catch (CommonException e) {
            model.addAttribute("error", e.getMessage()); // 사용자에게 오류 메시지 전달
            return "member/find-idpw";
        }

    }

    // 아이디 + 비번 합쳐서 인증 코드 검증
    @PostMapping("/auth-idpw-verification")
    public String findIdCodeVerification(@RequestParam String email, Model model,
        @RequestParam(required = false) String userId,
        @RequestParam String code, HttpSession session) {

        // 인증번호가 입력된 상태 → 인증번호 검증 단계
        String sessionCode, sessionEmail, sessionUserId;
        if (userId != null) {
            // 비밀번호 찾기
            sessionCode = (String) session.getAttribute("pwAuthCode");
            sessionEmail = (String) session.getAttribute("pwAuthEmail");
            sessionUserId = (String) session.getAttribute("pwAuthUserId");
        } else {
            // 아이디 찾기
            sessionCode = (String) session.getAttribute("idAuthCode");
            sessionEmail = (String) session.getAttribute("idAuthEmail");
            sessionUserId = null;
        }

        // 인증 성공!!
        if (sessionCode != null && sessionCode.equals(code)) {
            // 세션 제거
            session.removeAttribute("authCode");
            model.addAttribute("isSuccess", true);
            if( userId == null) {
                try {
                    // 아이디 찾기
                    userId = memberService.findUserIdByEmailFromSession(session);
                    log.info("userId : " + userId);
                    model.addAttribute("message", userId);
                    return "member/find-idpw-verification";
                } catch (CommonException e) {
                    model.addAttribute("error", e.getMessage());
                    model.addAttribute("step", null);
                    return "member/find-idpw";
                }
            } else {
                // 비번 찾기
                model.addAttribute("email", sessionEmail);
                model.addAttribute("userId", sessionUserId);
                model.addAttribute("step", "verify");
                return "member/reset-password";
            }
        }else {
            // 인증 실패!!
            if (userId != null) {
                // 비번 찾기
                model.addAttribute("userId", userId);
                model.addAttribute("type", "pw");
            }
            model.addAttribute("error", "인증번호가 일치하지 않습니다.");
            model.addAttribute("email", email);
            model.addAttribute("step", "verify");
            model.addAttribute("isSuccess", false);
            return "member/find-idpw";
        }
    }

    @PostMapping("/change-pw")
    public String changePw(@RequestParam String userId,
        @RequestParam String email,
        @RequestParam String newPassword,
        @RequestParam String confirmPassword,
        Model model) {

        // 1. 비밀번호 일치 여부 확인
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("userId", userId);
            model.addAttribute("email", email);
            return "member/reset-password";
        }

        try {
            memberService.changePassword(userId, email, newPassword);
            return "member/find-idpw-verification";

        } catch (CommonException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", userId);
            model.addAttribute("email", email);
            return "member/reset-password";
        }
    }

}
