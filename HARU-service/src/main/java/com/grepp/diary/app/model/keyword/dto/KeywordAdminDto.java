package com.grepp.diary.app.model.keyword.dto;

import com.grepp.diary.app.model.keyword.code.KeywordType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordAdminDto {
    private Integer keywordId;
    private String name;
    private Boolean isUse;
    private KeywordType keywordType;
    private Integer count;
    private LocalDateTime deletedAt;
}
