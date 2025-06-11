package com.grepp.diary.app.model.custom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomAiInfoDto {
    private String name;
    private String savePath;
    private String renamedName;

    public String getRenamedPath(){
        return savePath != null && renamedName != null ? "/uploads/" + savePath + renamedName : null;
    }
}
