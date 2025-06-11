package com.grepp.diary.app.model.keyword.dto;

import com.grepp.diary.app.model.keyword.code.KeywordType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeywordAdminDto {
    private Integer keywordId;
    private String name;
    private Boolean isUse;
    private KeywordType keywordType;
    private Integer count;

    public KeywordAdminDto(Integer keywordId, String name, Boolean isUse, KeywordType keywordType, Integer count) {
        this.keywordId = keywordId;
        this.name = name;
        this.isUse = isUse;
        this.keywordType = keywordType;
        this.count = count;
    }

}
