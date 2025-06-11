package com.grepp.diary.app.model.ai.dto;

import com.grepp.diary.app.model.ai.entity.AiImg;
import com.grepp.diary.app.model.common.code.ImgType;
import com.grepp.diary.infra.util.file.FileDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AiImgDto {

    private Integer aiImgId;
    private String savePath;
    private ImgType type;
    private String originName;
    private String renamedName;
    private Boolean isUSe;

    public AiImgDto(ImgType type, FileDto fileDto) {
        this.type = type;
        this.originName = fileDto.originFileName();
        this.renamedName = fileDto.renameFileName();
        this.savePath = fileDto.savePath();
    }

    public static AiImgDto fromEntity(AiImg aiImg) {
        AiImgDto dto = new AiImgDto();
        dto.setAiImgId(aiImg.getAiImgId());
        dto.setSavePath(aiImg.getSavePath());
        dto.setOriginName(aiImg.getOriginName());
        dto.setIsUSe(aiImg.getIsUse());
        dto.setRenamedName(aiImg.getRenamedPath());
        dto.setType(aiImg.getType());
        return dto;
    }
}
