package com.grepp.diary.app.model.diary.dto;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.keyword.dto.KeywordInfoDto;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DiaryEmotionStatsDto {
    private Integer diaryId;
    private Emotion emotion;
    private List<KeywordInfoDto> keywordInfoDtos;
    private LocalDate date;
}
