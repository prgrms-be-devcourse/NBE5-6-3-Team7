package com.grepp.diary.app.controller.api.diary.payload;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.entity.Diary;
import java.time.LocalDate;
import java.util.List;

public record DiaryDailyEmotionResponse(
    List<DiaryDailyEmotion> diaryDailyEmotionList
) {
    public record DiaryDailyEmotion(
        LocalDate date,
        Emotion emotion
    ) {
        public static DiaryDailyEmotion fromEntity(Diary diary) {
            return new DiaryDailyEmotion(
                diary.getDate(),
                diary.getEmotion()
            );
        }
    }

    public static DiaryDailyEmotionResponse fromEntityList(List<Diary> diaries) {
        List<DiaryDailyEmotion> diaryDailyEmotionList = diaries.stream().map(DiaryDailyEmotion::fromEntity).toList();
        if (diaryDailyEmotionList.isEmpty()) return null;
        return new DiaryDailyEmotionResponse(diaryDailyEmotionList);
    }
}
