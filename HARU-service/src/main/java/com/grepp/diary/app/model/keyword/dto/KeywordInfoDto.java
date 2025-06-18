package com.grepp.diary.app.model.keyword.dto;

import com.grepp.diary.app.model.keyword.code.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class KeywordInfoDto {
    private String name;
    private KeywordType type;
}
