package com.grepp.diary.app.controller.web.member;

import com.grepp.diary.app.controller.web.member.form.SettingPasswordForm;
import com.grepp.diary.app.controller.web.member.form.SettingEmailForm;
import com.grepp.diary.app.model.ai.AiService;
import com.grepp.diary.app.model.ai.dto.AiDto;
import com.grepp.diary.app.model.auth.domain.Principal;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {


    private final MemberService memberService;
    private final CustomService customService;
    private final AiService aiService;

    @GetMapping("/onboarding-qna")
    public String showOnboardingQnaPqge() {
        return "onboarding/onboarding-qna";
    }

    @GetMapping("/onboarding-result")
    public String showOnboardingResultPage(HttpSession session, Model model) {
        Integer aiId = (Integer) session.getAttribute("aiId");
        String userId = (String) session.getAttribute("userId");

        // 사용자이름 조회
        Member member = memberService.getMemberByUserId(userId);
        String name = member.getName();

        // AI 조회
        AiDto ai = aiService.getSingleAi(aiId);

        model.addAttribute("ai", ai);
        model.addAttribute("name", name);
        return "onboarding/onboarding-result";
    }

    @PostMapping("/custom-ai")
    public String registerCustomAiResult(@RequestParam("aiId") int aiId,
        Authentication authentication,
        HttpSession session) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        String userId = authentication.getName();

        boolean isFormal = "1".equals(String.valueOf(session.getAttribute("isFormal")));
        boolean isLong = "1".equals(String.valueOf(session.getAttribute("isLong")));

        customService.registerCustomSettings(userId, aiId, isFormal, isLong);

        return "redirect:/app";
    }

    // 회원 이메일 변경 요청
    @PostMapping("/settings/update-email")
    public String updateEmail(
        Authentication authentication,
        @Valid @ModelAttribute("emailForm") SettingEmailForm settingEmailForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if(bindingResult.hasErrors()) {
            return "redirect:/app/settings/email";
        }

        String userId = authentication.getName();

        // 비밀번호 확인 실패시
        if(!memberService.validUser(userId, settingEmailForm.getPassword())) {
            bindingResult.rejectValue("password", "password.invalid", "비밀번호가 일치하지 않습니다");
            return "redirect:/app/settings/email";
        }

        if(memberService.isExist(settingEmailForm.getNewEmail())){
            bindingResult.rejectValue("newEmail", "email already exists", "이미 사용중인 이메일입니다");
            redirectAttributes.addFlashAttribute("message", "이미 사용중인 이메일입니다");
            return "redirect:/app/settings/email";
        }

        boolean isSuccess = memberService.updateEmail(userId, settingEmailForm.getNewEmail());
        if(!isSuccess) {
            redirectAttributes.addFlashAttribute("message", "이메일 변경도중 문제가 발생하였습니다");
            return "redirect:/app/settings/email";
        }
        redirectAttributes.addFlashAttribute("message", "성공적으로 이메일을 변경하였습니다");
        return "redirect:/app/settings";
    }

    // 회원 비밀번호 변경 요청
    @PostMapping("/settings/update-password")
    public String updatePassword(
        @Valid @ModelAttribute("passwordForm") SettingPasswordForm settingPasswordForm,
        Authentication authentication,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if(bindingResult.hasErrors()) {
            return "redirect:/app/settings/password";
        }

        String userId = authentication.getName();
        System.out.println("[DEBUG]" + settingPasswordForm.getCurrentPassword());
        System.out.println("[DEBUG]" + settingPasswordForm.getNewPassword());
        System.out.println("[DEBUG]" + settingPasswordForm.getCheckPassword());

        // 유저 검증
        if(!memberService.validUser(userId, settingPasswordForm.getCurrentPassword())) {
            bindingResult.rejectValue("currentPassword", "currentPassword.invalid", "비밀번호가 일치하지 않습니다.");
            System.out.println("[DEBUG] error while validating user");
            return "redirect:/app/settings/password";
        }

        boolean isSuccess = memberService.updatePassword(userId, settingPasswordForm.getNewPassword());
        if(!isSuccess) {
            redirectAttributes.addFlashAttribute("message", "비밀번호 변경 도중 문제가 발생하였습니다");
            System.out.println("[DEBUG] error while updating password");
            return "redirect:/app/settings/password";
        }
        redirectAttributes.addFlashAttribute("message", "성공적으로 비밀번호를 변경하였습니다");
        return "redirect:/app/settings";
    }

    // 1. 탈퇴 확인 페이지 렌더링
    @GetMapping("/leave")
    public String showLeavePage(@AuthenticationPrincipal Principal principal, Model model) {
        String aiName = memberService.findAiNameByUserId(principal.getUsername());
        model.addAttribute("aiName", aiName);
        return "member/leave";
    }

    @PostMapping("/leave")
    public String deleteAccount(@AuthenticationPrincipal Principal principal,
        HttpSession session,
        RedirectAttributes redirectAttributes) {
        String userId = principal.getUsername();

        try {
            memberService.withdraw(userId);

            SecurityContextHolder.clearContext(); // 인증 정보 제거
            session.invalidate(); // 세션 무효화

            redirectAttributes.addFlashAttribute("message", "탈퇴가 완료되었습니다.");
            return "redirect:/member/leave-success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "탈퇴 중 오류가 발생했습니다.");
            return "redirect:/member/leave";
        }
    }


    @GetMapping("/leave-success")
    public String leaveSuccessPage(Model model) {
        return "member/leave-success";
    }
}
