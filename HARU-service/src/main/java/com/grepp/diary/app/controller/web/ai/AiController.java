package com.grepp.diary.app.controller.web.ai;

import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.ai.entity.AiImg;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.infra.util.xss.XssProtectionUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("ai")
public class AiController {

    private final DiaryService diaryService;
    private final CustomService customService;
    private final XssProtectionUtils xssUtils;


    @GetMapping("chat")
    public String chatView(
        @RequestParam int diaryId,
        Authentication authentication,
        Model model
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }
        String userId = authentication.getName();
        Diary diary = diaryService.getDiaryById(diaryId);
        String diaryWriter = diary.getMember().getUserId();
        if (!userId.equals(diaryWriter)) {
            return "redirect:/";
        }

        Optional<Custom> result = customService.findByUserId(userId);
        if (result.isEmpty()) {
            return "onboarding/onboarding";
        }
        Ai ai = result.get().getAi();
        AiImg aiImg = ai.getImages().getFirst();

        // 렌더링 시 이스케이프 처리 X -> 여기서 이스케이프
        model.addAttribute("diaryId", diaryId);
        model.addAttribute("diaryDate", diary.getDate());
        model.addAttribute("diaryReply", xssUtils.escapeHtmlWithLineBreaks(diary.getReply().getContent()));
        model.addAttribute("aiName", ai.getName());
        model.addAttribute("aiId", ai.getAiId());
        model.addAttribute("imgSavePath", aiImg.getSavePath());
        model.addAttribute("imgRenamedName", aiImg.getRenamedName());
        return "api/ai/chat";
    }

}
