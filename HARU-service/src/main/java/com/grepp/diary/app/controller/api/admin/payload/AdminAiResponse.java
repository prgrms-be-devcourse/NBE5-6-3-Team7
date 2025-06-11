package com.grepp.diary.app.controller.api.admin.payload;

import com.grepp.diary.app.model.ai.dto.AiWithCountDto;
import java.util.List;

public record AdminAiResponse(
    List<AiInfo> aiInfos
) {
    public record AiInfo(
        Integer aiId,
        String name,
        String mbti,
        String info,
        String prompt,
        Boolean isUse,
        Integer count
    ) {
        public static AiInfo fromDto(AiWithCountDto aiWithCountDto) {
            return new AiInfo(
                aiWithCountDto.getAiId(),
                aiWithCountDto.getName(),
                aiWithCountDto.getMbti(),
                aiWithCountDto.getInfo(),
                aiWithCountDto.getPrompt(),
                aiWithCountDto.getIsUse(),
                aiWithCountDto.getCount()
            );
        }
    }

    public static AdminAiResponse fromDtoList(List<AiWithCountDto> ais) {
        List<AiInfo> aiInfos = ais.stream().map(AiInfo::fromDto).toList();
        return new AdminAiResponse(aiInfos);
    }
}
