package com.grepp.diary.app.model.keyword.repository;

import com.grepp.diary.app.model.keyword.dto.KeywordAdminDto;
import com.querydsl.core.Tuple;
import java.time.LocalDateTime;
import java.util.List;

public interface KeywordRepositoryCustom {
    List<Tuple> getMostPopularKeywords();

    List<KeywordAdminDto> findKeywordsByType(String keywordType);

    List<Integer> activeKeywords(List<Integer> keywordIds);

    List<Integer> nonActiveKeywords(List<Integer> keywordIds);

    List<Tuple> findTop5KeywordsByUserIdAndDate(String userId, LocalDateTime start, LocalDateTime end);

}
