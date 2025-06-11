package com.grepp.diary.app.controller.api.keyword;

import com.grepp.diary.app.controller.api.keyword.payload.KeywordRankResponse;
import com.grepp.diary.app.model.keyword.KeywordService;
import com.grepp.diary.infra.util.date.DateUtil;
import com.grepp.diary.infra.util.date.dto.DateRangeDto;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/keyword")
public class KeywordApiController {
    private final KeywordService keywordService;
    private final DateUtil dateUtil;

    @GetMapping("/ranking")
    public KeywordRankResponse getKeywordRanking(
        Authentication authentication,
        @RequestParam String period,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date
    ) {
        DateRangeDto range = dateUtil.toDateRangeDto(period, date);
        LocalDate start = range.start();
        LocalDate end = range.end();
        String userId = authentication.getName();

        return KeywordRankResponse.fromDtoList(keywordService.getTop5Keywords(userId, start, end));
    }
}
