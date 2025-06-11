package com.grepp.diary.app.controller.web.app;

import com.grepp.diary.app.model.ai.AiService;
import com.grepp.diary.app.model.ai.dto.AiDto;
import com.grepp.diary.app.model.app.AppService;
import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("app")
public class AppController {

    private final AppService appService;
    private final AiService aiService;

    @GetMapping
    public String showHome(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        String userId = authentication.getName();

        String redirect = appService.getRedirectViewIfAdmin(userId);
        if (redirect != null) {
            return redirect;
        }

        List<AiDto> allAi = aiService.getAllAi();
        model.addAttribute("name", appService.getUserName(userId));
        model.addAttribute("allAi", allAi);

        return appService.isNewUser(userId)
            ? "onboarding/onboarding"
            : "app/home";
    }

    @GetMapping("/timeline")
    public String showTimeline(){
        return "app/timeline";
    }

    @GetMapping("/dashboard")
    public String showDashboard(){
        return "app/member-dashboard";
    }

    @GetMapping("/settings")
    public String showSetting(){
        return "app/settings/settings";
    }

    @GetMapping("/settings/email")
    public String showChangeEmail(){
        return "app/settings/settings-email";
    }

    @GetMapping("/settings/password")
    public String showChangePassword(){
        return "app/settings/settings-password";
    }

    @GetMapping("/settings/ai")
    public String showChangeAi(){
        return "app/settings/settings-ai";
    }
}
