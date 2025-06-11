package com.grepp.diary.app.controller.api.member;


import com.grepp.diary.app.controller.api.member.payload.OnboardingRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    @PostMapping("/onboarding-ai")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> receiveOnboardingChoice(
        @RequestBody OnboardingRequest request,
        Authentication authentication,
        HttpSession session) {

        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> response = new HashMap<>();
            response.put("redirect", "/");
            response.put("error", "unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String userId = authentication.getName();
        // 세션에 custom - ai 정보 저장
        session.setAttribute("userId", userId);
        session.setAttribute("aiId", request.getAiId());
        session.setAttribute("isFormal", request.getIsFormal());
        session.setAttribute("isLong", request.getIsLong());

        Map<String, Object> result = new HashMap<>();
        result.put("redirect", "/member/onboarding-result");
        return ResponseEntity.ok(result);
    }
}
