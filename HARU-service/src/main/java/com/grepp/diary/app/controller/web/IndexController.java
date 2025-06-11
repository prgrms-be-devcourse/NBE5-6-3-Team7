package com.grepp.diary.app.controller.web;

import com.grepp.diary.app.controller.web.auth.form.SigninForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
        if (!model.containsAttribute("signinForm")) {
            model.addAttribute("signinForm", new SigninForm());
        }

        // 로그인된 사용자면 /app으로 리다이렉트
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/app";
        }

        return "index";
    }
    
}
