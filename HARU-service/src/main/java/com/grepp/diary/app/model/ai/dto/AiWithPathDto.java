package com.grepp.diary.app.model.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class AiWithPathDto {
    private Integer aiId;
    private String savePath;
    private String renamedName;
}
