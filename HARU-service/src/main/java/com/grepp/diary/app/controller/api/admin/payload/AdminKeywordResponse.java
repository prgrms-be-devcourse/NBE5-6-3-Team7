package com.grepp.diary.app.controller.api.admin.payload;

import com.grepp.diary.app.model.keyword.code.KeywordType;
import com.grepp.diary.app.model.keyword.dto.KeywordAdminDto;
import java.util.List;

public record AdminKeywordResponse(
    List<KeywordInfo> keywordInfos
) {
    public record KeywordInfo(
        Integer keywordId,
        String name,
        Boolean isUse,
        KeywordType keywordType,
        Integer count
    ) {
        public static KeywordInfo fromDto(KeywordAdminDto keywordAdminDto) {
            return new KeywordInfo(
                keywordAdminDto.getKeywordId(),
                keywordAdminDto.getName(),
                keywordAdminDto.getIsUse(),
                keywordAdminDto.getKeywordType(),
                keywordAdminDto.getCount()
            );
        }
    }

    public static AdminKeywordResponse fromDtoList(List<KeywordAdminDto> keywords) {
        List<KeywordInfo> keywordInfos = keywords.stream().map(KeywordInfo::fromDto).toList();
        return new AdminKeywordResponse(keywordInfos);
    }
}
