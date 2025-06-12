package com.grepp.diary.app.controller.api.dashboard.payload;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.dto.DiaryEmotionAvgDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.infra.util.date.code.DatePeriod;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmotionFlowResponse {

    private DatePeriod type;
    private Object data;

    public EmotionFlowResponse(DatePeriod type, Object data) {
        this.type = type;
        this.data = data != null ? data : Collections.emptyList();
    }

    public static EmotionFlowResponse fromDiariesOnMonth(DatePeriod type, List<Diary> diaries) {
        List<DiaryMonthlyEmotion> list = diaries.stream()
            .filter(d -> d.getEmotion() != null && d.getDate() != null)
            .map(d -> new DiaryMonthlyEmotion(d.getDate(), d.getEmotion()))
            .sorted(Comparator.comparing(DiaryMonthlyEmotion::date))  // 정렬도 가능
            .toList();

        return new EmotionFlowResponse(type, list);
    }

    public static EmotionFlowResponse fromDiariesOnYear(DatePeriod type, List<DiaryEmotionAvgDto> avgs){
        List<DiaryYearlyEmotion> list = avgs.stream()
            .map(d -> new DiaryYearlyEmotion(d.getMonth(), d.getAvg()))
            .sorted(Comparator.comparing(DiaryYearlyEmotion::month))
            .toList();

        return new EmotionFlowResponse(type, list);
    }

    public record DiaryMonthlyEmotion(LocalDate date, Emotion emotion) {}
    public record DiaryYearlyEmotion(Integer month, Double average){}
}
