package com.grepp.diary.app.controller.api.diary.payload;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.dto.DiaryEmotionCountDto;
import java.util.List;

public record DiaryEmotionCountResponse(
    List<DiaryEmotionCount> diaryEmotionCountList
) {
    public record DiaryEmotionCount(Emotion emotion, Integer emotionCount) {
        public static DiaryEmotionCount fromDto(DiaryEmotionCountDto dto) {
            return new DiaryEmotionCount(dto.getEmotion(), dto.getEmotionCount());
        }
    }
    public static DiaryEmotionCountResponse fromDtoList(List<DiaryEmotionCountDto> diaryEmotionCountDtoList) {
        List<DiaryEmotionCount> diaryEmotionCountList = diaryEmotionCountDtoList.stream().map(DiaryEmotionCount::fromDto).toList();
        if (diaryEmotionCountList.isEmpty()) {return null;}
        return new DiaryEmotionCountResponse(diaryEmotionCountList);
    }
}
