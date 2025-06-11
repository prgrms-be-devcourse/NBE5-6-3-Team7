package com.grepp.diary.app.controller.api.ai;

import com.grepp.diary.app.controller.api.ai.payload.AiListResponse;
import com.grepp.diary.app.controller.api.ai.AiRequestQueue.AiRequestTask;
import com.grepp.diary.app.controller.api.ai.payload.ChatRequest;
import com.grepp.diary.app.controller.api.ai.payload.Message;
import com.grepp.diary.app.model.ai.AiChatService;
import com.grepp.diary.app.model.ai.AiService;
import com.grepp.diary.app.model.ai.AiReplyScheduler;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.chat.ChatService;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.infra.util.xss.XssProtectionUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/ai")
public class AiApiController {

    private final AiChatService aiChatService;
    private final AiService aiService;
    private final DiaryService diaryService;
    private final AiReplyScheduler aiReplyScheduler;
    private final AiRequestQueue aiRequestQueue;
    private final ChatService chatService;
    private final XssProtectionUtils xssUtils;

    @GetMapping("reply")
    public String singleReply(@RequestParam int diaryId){
        String prompt = aiReplyScheduler.buildReplyPrompt(diaryId);
        String replyContent = aiChatService.reply(prompt);

        log.info("prompt : {}", prompt);
        log.info("reply: {}", replyContent);
        diaryService.registReply(diaryId, replyContent);
        return replyContent;
    }

    @GetMapping("retry-batch")
    public String retryFailedReplies() {
        List<DiaryDto> failedDiaries = diaryService.getNoReplyDtos();
        if (failedDiaries.isEmpty()) {
            return "No failed replies to process";
        }
        log.info("Start retry for {} failed replies", failedDiaries.size());
        aiReplyScheduler.schedulingBatchProcess(failedDiaries, 0);
        return "Complete retry for " + failedDiaries.size() + " failed replies";
    }

    @PostMapping("chat")
    public CompletableFuture<String> chatWithAi(@RequestBody ChatRequest chatRequest) {
        Diary diary = diaryService.getDiaryById(chatRequest.getDiaryId());
        String prompt = buildChatPrompt(diary, chatRequest.getChatHistory(), chatRequest.getUserMessage());
        log.info("prompt : {}", prompt);

        CompletableFuture<String> future = new CompletableFuture<>();
        aiRequestQueue.addRequest(
            new AiRequestTask(() -> {
                String response = aiChatService.chat(prompt);
                // 렌더링 시 이스케이프 처리 X -> 여기서 이스케이프
                return xssUtils.escapeHtmlWithLineBreaks(response);
            }, future)
        );

        return future;
    }

    @PostMapping("chat/memo")
    public CompletableFuture<String> chatMemo(@RequestBody ChatRequest chatRequest) {
        int diaryId = chatRequest.getDiaryId();
        int chatCount = chatRequest.getChatHistory().size();
        log.info("chatCount : {}", chatCount);
        Diary diary = diaryService.getDiaryById(diaryId);
        String userId = diary.getMember().getUserId();
        Integer replyId = diary.getReply().getReplyId();
        String prompt = buildMemoPrompt(diary, chatRequest.getChatHistory());
        log.info("prompt : {}", prompt);

        CompletableFuture<String> future = new CompletableFuture<>();
        aiRequestQueue.addRequest(
            new AiRequestTask(() -> {
                String memo = aiChatService.memo(prompt);
                log.info("diary id: {}", diaryId);
                log.info("memo content: {}", memo);
                diaryService.registReply(diaryId, memo);
                chatService.registCount(userId, replyId, chatCount);
                return memo;
            }, future)
        );
        return future;
    }

    @PostMapping("chat/end")
    public String chatEnd(@RequestBody ChatRequest chatRequest) {
        int diaryId = chatRequest.getDiaryId();
        int chatCount = chatRequest.getChatHistory().size();
        Diary diary = diaryService.getDiaryById(diaryId);
        String userId = diary.getMember().getUserId();
        Integer replyId = diary.getReply().getReplyId();

        chatService.registCount(userId, replyId, chatCount);
        log.info("Updated chat count {} for diaryId {}", chatCount, diaryId);

        return "Updated chat count";
    }

    private String buildMemoPrompt(Diary diary, List<Message> chatHistory) {
        StringBuilder prompt = buildInitialPrompt(diary);
        appendChatHistory(prompt, chatHistory, true, null); // AI 답변 unescape, 사용자 메시지 없음
        return prompt.toString();
    }

    private String buildChatPrompt(Diary diary, List<Message> chatHistory, String userMessage) {
        StringBuilder prompt = buildInitialPrompt(diary);
        appendChatHistory(prompt, chatHistory, false, userMessage); // AI 답변 그대로, 사용자 메시지 포함
        return prompt.toString();
    }

    private StringBuilder buildInitialPrompt(Diary diary) {
        Member member = diary.getMember();
        Custom custom = member.getCustom();
        Ai ai = custom.getAi();

        StringBuilder prompt = aiReplyScheduler.buildCustomAiPrompt(ai, custom);
        prompt.append("\n사용자가 작성했던 일기 내용: ").append(diary.getContent());
        prompt.append("\n당신이 작성했던 일기 답변: ").append(diary.getReply().getContent());

        return prompt;
    }

    private void appendChatHistory(StringBuilder prompt, List<Message> chatHistory, boolean unescapeAi, String userMessage) {
        // 이전 대화 내역
        prompt.append("\n--대화 내용--");
        if (chatHistory != null && !chatHistory.isEmpty()) {
            for (Message msg : chatHistory) {
                prompt.append("\n사용자: ").append(msg.getUser());
                String aiResponse = unescapeAi
                    ? xssUtils.unescapeHtmlWithLineBreaks(msg.getAi())
                    : msg.getAi();
                prompt.append("\n당신: ").append(aiResponse);
            }
        }

        // 사용자 메시지
        if (userMessage != null) {
            prompt.append("\n사용자: ").append(userMessage);
            prompt.append("\n당신: ");
        }
    }

    @GetMapping("/list")
    private AiListResponse getAiInfoList() {
        return AiListResponse.fromDtoList(aiService.getAIList());
    }

}
