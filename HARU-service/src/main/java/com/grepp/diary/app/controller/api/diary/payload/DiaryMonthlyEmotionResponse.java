package com.grepp.diary.app.controller.api.diary.payload;

import com.grepp.diary.app.model.diary.dto.DiaryEmotionAvgDto;
import java.util.List;

public record DiaryMonthlyEmotionResponse (
    List<DiaryMonthlyEmotion> diaryMonthlyEmotions
) {
    public record DiaryMonthlyEmotion (
        int month,
        double average
    ) {
        public static DiaryMonthlyEmotion fromDto(DiaryEmotionAvgDto emotionAvgDto) {
            return new DiaryMonthlyEmotion(
                emotionAvgDto.getMonth(),
                emotionAvgDto.getAvg()
            );
        }
    }

    public static DiaryMonthlyEmotionResponse fromDtoList(List<DiaryEmotionAvgDto> emotionAvgDtoList) {
        List<DiaryMonthlyEmotion> diaryMonthlyEmotions = emotionAvgDtoList.stream().map(DiaryMonthlyEmotion::fromDto).toList();
        if(diaryMonthlyEmotions.isEmpty()) {return null;}
        return new DiaryMonthlyEmotionResponse(diaryMonthlyEmotions);
    }
}
