package com.grepp.diary.app.model.custom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
@AllArgsConstructor
public class CustomAIDto {

    private Integer aiId;
    private boolean isFormal;
    private boolean isLong;
}
