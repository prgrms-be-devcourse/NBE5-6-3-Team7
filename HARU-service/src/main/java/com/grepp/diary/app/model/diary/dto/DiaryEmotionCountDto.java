package com.grepp.diary.app.model.diary.dto;

import com.grepp.diary.app.model.diary.code.Emotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiaryEmotionCountDto {
    private Emotion emotion;
    private Integer emotionCount;
}
