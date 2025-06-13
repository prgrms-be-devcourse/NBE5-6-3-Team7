package com.grepp.diary.app.controller.api.dashboard;

import com.grepp.diary.app.controller.api.dashboard.payload.EmotionFlowResponse;
import com.grepp.diary.app.controller.api.dashboard.payload.DiaryEmotionCountResponse;
import com.grepp.diary.app.controller.api.dashboard.payload.KeywordRankResponse;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.keyword.KeywordService;
import com.grepp.diary.infra.util.date.DateUtil;
import com.grepp.diary.infra.util.date.code.DatePeriod;
import com.grepp.diary.infra.util.date.dto.DateRangeDto;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DiaryService diaryService;
    private final KeywordService keywordService;
    private final DateUtil dateUtil;

    @GetMapping("/emotion-flow")
    public ResponseEntity<?> getEmotionFlow(
        Authentication authentication,
        @RequestParam DatePeriod type,
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false) Integer year
    ) {
        String userId = authentication.getName();

        if(type == DatePeriod.MONTH) {
            DateRangeDto range = dateUtil.toDateRangeDto(type, date);
            LocalDate start = range.start();
            LocalDate end = range.end();

            return ResponseEntity.ok(EmotionFlowResponse.fromDiariesOnMonth(
                type,
                diaryService.getDiariesDateBetween(userId, start, end)
            ));
        } else if (type == DatePeriod.YEAR) {
            return ResponseEntity.ok(EmotionFlowResponse.fromDiariesOnYear(
                type,
                diaryService.getMonthlyEmotionAvgOfYear(userId, year)
            ));
        }

        return ResponseEntity.badRequest().body("Invalid type");
    }

    // 월간/연간 작성된 일기수 데이터 API
    @GetMapping("/diary-count")
    public int getDiaryCount(
        Authentication authentication,
        @RequestParam DatePeriod period,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date
    ){
        DateRangeDto range = dateUtil.toDateRangeDto(period, date);
        LocalDate start = range.start();
        LocalDate end = range.end();
        String userId = authentication.getName();

        return diaryService.getUserDiaryCount(userId, start, end);
    }

    // 특정 기간내의 작성된 일기 기준 감정별 개수 API
    @GetMapping("/emotion-count")
    public ResponseEntity<?> getEmotionCount(
        Authentication authentication,
        @RequestParam DatePeriod period,
        @RequestParam int value
    ) {
        String userId = authentication.getName();

        return ResponseEntity.ok(DiaryEmotionCountResponse.from(
            period,
            diaryService.getEmotionsCount(userId, period, value)
        ));
    }

    // 특정 기간내의 상위 5개의 키워드 랭킹 데이터
    @GetMapping("/keyword-ranking")
    public ResponseEntity<?> getKeywordRanking(
        Authentication authentication,
        @RequestParam DatePeriod period,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date
    ) {
        DateRangeDto range = dateUtil.toDateRangeDto(period, date);
        LocalDate start = range.start();
        LocalDate end = range.end();
        String userId = authentication.getName();

        return ResponseEntity.ok(KeywordRankResponse.from(
            period,
            keywordService.getTop5Keywords(userId, start, end)
        ));
    }
}
