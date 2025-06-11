package com.grepp.diary.app.model.app;

import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class AppService {

    private final MemberService memberService;
    private final CustomService customService;

    public String getRedirectViewIfAdmin(String userId) {
        Member member = memberService.getMemberByUserId(userId);
        if (member.getRole() == Role.ROLE_ADMIN) {
            return "redirect:/admin";
        }
        return null;
    }

    public String getUserName(String userId) {
        return memberService.getMemberByUserId(userId).getName();
    }

    public boolean isNewUser(String userId) {
        return customService.findByUserId(userId).isEmpty();
    }
}

