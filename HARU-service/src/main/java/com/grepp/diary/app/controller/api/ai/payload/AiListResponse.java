package com.grepp.diary.app.controller.api.ai.payload;

import com.grepp.diary.app.model.ai.dto.AiInfoDto;
import java.util.List;

public record AiListResponse(
    List<AiInfo> aiInfoList
) {
    public record AiInfo(
        Integer id,
        String name,
        String mbti,
        String info,
        String renamedPath
    ) {
        public static AiInfo fromDto(AiInfoDto aiInfoDto) {
            String imagePath = null;

            imagePath = aiInfoDto.getRenamedPath();

            return new AiInfo(
                aiInfoDto.getAiId(),
                aiInfoDto.getName(),
                aiInfoDto.getMbti(),
                aiInfoDto.getInfo(),
                imagePath
            );
        }
    }
    public static AiListResponse fromDtoList(List<AiInfoDto> aiInfoDtoList) {
        List<AiInfo> aiInfoList = aiInfoDtoList.stream().map(AiInfo::fromDto).toList();
        return new AiListResponse(aiInfoList);
    }
}
