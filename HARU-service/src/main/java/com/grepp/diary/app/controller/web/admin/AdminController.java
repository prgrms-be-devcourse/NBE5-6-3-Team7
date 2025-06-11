package com.grepp.diary.app.controller.web.admin;

import com.grepp.diary.app.model.ai.dto.AiWithCountDto;
import com.grepp.diary.app.model.chat.ChatService;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.keyword.KeywordService;
import com.grepp.diary.app.model.keyword.dto.KeywordAdminDto;
import com.grepp.diary.app.model.member.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {

    private final KeywordService keywordService;
    private final MemberService memberService;
    private final CustomService customService;
    private final DiaryService diaryService;
    private final ChatService chatService;

    @GetMapping
    public String index(Model model) {
        List<KeywordAdminDto> popularKeywords = keywordService.getMostPopular();
        Integer memberCount = memberService.getAllMemberCount();
        Integer monthDiaryCount = diaryService.getMonthDiariesCount();
        List<AiWithCountDto> popularCustoms = customService.getAiByLimit(5);
        Integer monthChatCount = chatService.getMonthChatCount();

        model.addAttribute("popularKeywords", popularKeywords);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("monthDiaryCount", monthDiaryCount);
        model.addAttribute("popularCustoms", popularCustoms);
        model.addAttribute("monthChatCount", monthChatCount);

        return "admin/admin-index";
    }

    @GetMapping("keyword")
    public String keyword() {

        return "admin/admin-keyword";
    }

    @GetMapping("ai")
    public String ai() {

        return "admin/admin-ai";
    }

    @GetMapping("ai/write")
    public String aiWrite() {

        return "admin/admin-ai-write";
    }
}
