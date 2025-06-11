package com.grepp.diary.app.controller.api.keyword.payload;

import com.grepp.diary.app.model.keyword.dto.KeywordDto;
import java.util.List;

public record KeywordRankResponse(
    List<KeywordRank> keywordRankList
) {
    public record KeywordRank(
        String name,
        Integer count
    ){
        public static KeywordRank fromDto(KeywordDto keywordDto) {
            return new KeywordRank(keywordDto.getName(), keywordDto.getCount());
        }
    }

    public static KeywordRankResponse fromDtoList(List<KeywordDto> keywordDtoList) {
        List<KeywordRank> keywordRankList = keywordDtoList.stream().map(KeywordRank::fromDto).toList();
        if(keywordRankList.isEmpty()) return null;
        return new KeywordRankResponse(keywordRankList);
    }
}
