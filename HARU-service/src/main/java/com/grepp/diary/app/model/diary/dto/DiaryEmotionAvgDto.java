package com.grepp.diary.app.model.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class DiaryEmotionAvgDto {
    Integer month;
    Double avg;
}
