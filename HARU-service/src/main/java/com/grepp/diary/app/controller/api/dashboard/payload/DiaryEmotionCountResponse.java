package com.grepp.diary.app.controller.api.dashboard.payload;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.dto.DiaryEmotionCountDto;
import com.grepp.diary.infra.util.date.code.DatePeriod;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiaryEmotionCountResponse {

    private DatePeriod type;
    private Object data;

    public DiaryEmotionCountResponse(DatePeriod type, Object data) {
        this.type = type;
        this.data = data != null ? data : Collections.emptyList();
    }

    public static DiaryEmotionCountResponse from(DatePeriod type, List<DiaryEmotionCountDto> dto) {
        List<DiaryEmotionCount> list = dto.stream()
            .map(d -> new DiaryEmotionCount(d.getEmotion(), d.getEmotionCount()))
            .toList();

        return new DiaryEmotionCountResponse(type, list);
    }

    public record DiaryEmotionCount(Emotion emotion, Integer emotionCount){}
}
