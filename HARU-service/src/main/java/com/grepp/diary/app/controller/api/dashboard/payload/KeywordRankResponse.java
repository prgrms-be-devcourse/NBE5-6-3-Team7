package com.grepp.diary.app.controller.api.dashboard.payload;

import com.grepp.diary.app.model.keyword.dto.KeywordDto;
import com.grepp.diary.infra.util.date.code.DatePeriod;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeywordRankResponse {

    private DatePeriod type;
    private Object data;

    public KeywordRankResponse(DatePeriod type, Object data) {
        this.type = type;
        this.data = data != null ? data : Collections.emptyList();
    }

    public static KeywordRankResponse from(DatePeriod type, List<KeywordDto> keywordDto) {
        List<KeywordRank> list = keywordDto.stream()
            .map(k -> new KeywordRank(k.getName(), k.getCount()))
            .sorted(Comparator
                .comparing(KeywordRank::count).reversed()
                .thenComparing(KeywordRank::name))
            .toList();

        return new KeywordRankResponse(type, list);
    }

    public record KeywordRank(String name, Integer count){}
}
