package com.grepp.diary.app.model.ai.dto;

import com.grepp.diary.app.model.ai.entity.Ai;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AiDto {
    private Integer aiId;
    private String name;
    private String mbti;
    private String info;
    private String prompt;
    private Boolean isUse;
    private List<AiImgDto> images;

    public static AiDto fromEntity(Ai ai) {
        AiDto dto = new AiDto();
        dto.setAiId(ai.getAiId());
        dto.setName(ai.getName());
        dto.setMbti(ai.getMbti());
        dto.setInfo(ai.getInfo());
        dto.setPrompt(ai.getPrompt());
        dto.setIsUse(ai.getIsUse());

        if (ai.getImages() != null) {
            List<AiImgDto> imageDtos = ai.getImages().stream()
                .map(AiImgDto::fromEntity)
                .toList();
            dto.setImages(imageDtos);
        }

        return dto;
    }
}
