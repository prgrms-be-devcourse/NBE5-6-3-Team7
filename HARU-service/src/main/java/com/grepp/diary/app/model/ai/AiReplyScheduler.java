package com.grepp.diary.app.model.ai;

import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.infra.util.xss.XssProtectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiReplyScheduler {

    private static final int BATCH_SIZE = 20; // RPM 30 보다 약간 작게
    private static final long DELAY_BTW_BATCHES = 60 * 1000; // 1분 대기

    private final DiaryService diaryService;
    private final MemberService memberService;
    private final AiChatService aiChatService;
    private final XssProtectionUtils xssUtils;

    // 자동 실행 메서드
//    @Scheduled(cron = "0 0 3 * * *")
    @Scheduled(cron = "0 */5 * * * *")
//    @Scheduled(fixedRate = 5000)  // 5초 마다
    public void autoReplyProcess() {
        log.info("Starting the diary reply process");
        initiateReplyProcess();
    }

    public void initiateReplyProcess() {
        List<DiaryDto> pendingDiaries = diaryService.getNoReplyDtos();
        log.info("Found {} diaries without replies", pendingDiaries.size());

        if (pendingDiaries.isEmpty()) {
            return;
        }
        schedulingBatchProcess(pendingDiaries, 0);
    }

    // 배치 별 스케줄 관리
    public void schedulingBatchProcess(List<DiaryDto> diaries, int startIndex) {
        if (startIndex >= diaries.size()) {
            log.info("All batches have been processed");
            return;
        }

        // 이번 배치 추출
        int endIndex = Math.min(startIndex + BATCH_SIZE, diaries.size());
        List<DiaryDto> currentBatch = diaries.subList(startIndex, endIndex);

        // 이번 배치 처리
        batchProcess(currentBatch);

        // 다음 배치 스케줄링
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                schedulingBatchProcess(diaries, endIndex);
            }
        };
        timer.schedule(timerTask, DELAY_BTW_BATCHES); // 배치 간 딜레이 설정
    }

    private void batchProcess(List<DiaryDto> batch) {
        log.info("Processing batch of {} diaries", batch.size());
        List<Integer> failedIds = new ArrayList<>();

        for (DiaryDto dto : batch) {
            try {
                Integer diaryId = dto.getDiaryId();
                String prompt = buildReplyPrompt(diaryId);
                String replyContent = aiChatService.reply(prompt);

                log.info("Processed diary id: {}", diaryId);
                diaryService.registReply(diaryId, replyContent);

                Thread.sleep(300); // 개별 요청 사이에도 약간의 텀
            } catch (Exception e) {
                log.error("Reply failed for diary id {}: {}", dto.getDiaryId(), e.getMessage());
                failedIds.add(dto.getDiaryId());
            }
        }

        if (!failedIds.isEmpty()) {
            log.warn("Failed diary IDs in this batch: {}", failedIds);
        }
    }

    // 일기 답장 프롬프트
    public String buildReplyPrompt(int diaryId) {
        DiaryDto dto = diaryService.getDto(diaryId);
        String content = dto.getContent();
        String userId = dto.getUserId();

        Member member = memberService.getMemberByUserId(userId);
        Custom custom = member.getCustom();
        Ai ai = custom.getAi();

        StringBuilder prompt = buildCustomAiPrompt(ai, custom);
        return prompt.append("\n일기 내용: ").append(content).toString();
    }

    // 기본 AI 프롬프트 + 옵션
    public StringBuilder buildCustomAiPrompt(Ai ai, Custom custom){
        StringBuilder prompt = new StringBuilder(ai.getPrompt());

        if (custom.isFormal()) {
            prompt.append(" 사용자에게는 존댓말로 정중하게 이야기합니다. 따뜻하고 배려 있는 어투를 사용합니다.")
                .append(" 스스로를 지칭할 땐 '저'를 사용하고 사용자를 지칭할 땐 '당신'을 사용합니다.");
        } else {
            prompt.append(" 사용자에게는 반말로, 친구처럼 다정하고 편안한 말투로 이야기합니다.")
                .append(" 스스로를 지칭할 땐 '나'를 사용하고 사용자를 지칭할 땐 '너'를 사용합니다.");
        }

        if (custom.isLong()) {
            prompt.append(" 답변은 감정이나 상황을 충분히 설명할 수 있도록 길고 풍부하게 작성하되, 공백을 포함하여 약 500자 분량으로 작성합니다.");
        } else {
            prompt.append(" 답변은 감정과 핵심 메시지를 적절히 전달할 수 있도록 간결하게 작성하되, 공백을 포함하여 약 300자 분량으로 작성합니다.");
        }
        return prompt;
    }

}
