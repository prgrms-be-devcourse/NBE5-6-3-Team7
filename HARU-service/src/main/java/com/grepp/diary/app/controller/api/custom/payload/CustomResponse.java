package com.grepp.diary.app.controller.api.custom.payload;

import com.grepp.diary.app.model.custom.dto.CustomAIDto;
import lombok.extern.slf4j.Slf4j;

public record CustomResponse(
    Integer aiId,
    boolean isFormal,
    boolean isLong
) {
    public static CustomResponse fromDto(CustomAIDto customAIDto) {
        return new CustomResponse(customAIDto.getAiId(), customAIDto.isFormal(), customAIDto.isLong());
    }
}
