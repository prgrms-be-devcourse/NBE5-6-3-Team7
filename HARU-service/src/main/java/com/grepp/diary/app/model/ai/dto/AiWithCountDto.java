package com.grepp.diary.app.model.ai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AiWithCountDto {
    private Integer aiId;
    private String name;
    private String mbti;
    private String info;
    private String prompt;
    private Boolean isUse;
    private Integer count;
}
