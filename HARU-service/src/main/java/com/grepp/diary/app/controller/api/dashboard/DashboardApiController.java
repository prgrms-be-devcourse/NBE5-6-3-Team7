package com.grepp.diary.app.controller.api.dashboard;

import com.grepp.diary.app.controller.api.dashboard.payload.EmotionFlowResponse;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.infra.util.date.DateUtil;
import com.grepp.diary.infra.util.date.code.DatePeriod;
import com.grepp.diary.infra.util.date.dto.DateRangeDto;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DiaryService diaryService;
    private final DateUtil dateUtil;

    @GetMapping("/emotion-flow")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getEmotionFlow(
//        Authentication authentication,
        @RequestParam DatePeriod type,
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false) Integer year
    ) {
//        String userId = authentication.getName();
        String userId = "user01";

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
}
