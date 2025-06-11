package com.grepp.diary.app.model.ai.dto;

import com.grepp.diary.app.model.ai.entity.Ai;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class AiInfoDto {

    private Integer aiId;
    private String name;
    private String mbti;
    private String info;
    private String savePath;
    private String renamedName;

    public String getRenamedPath(){
        return savePath != null && renamedName != null ? "/uploads/" + savePath + renamedName : null;
    }
}
