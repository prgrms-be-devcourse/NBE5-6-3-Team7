package com.grepp.diary.app.model.keyword.repository;

import com.grepp.diary.app.model.diary.entity.QDiary;
import com.grepp.diary.app.model.keyword.code.KeywordType;
import com.grepp.diary.app.model.keyword.dto.KeywordAdminDto;
import com.grepp.diary.app.model.keyword.entity.QDiaryKeyword;
import com.grepp.diary.app.model.keyword.entity.QKeyword;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KeywordRepositoryImpl implements KeywordRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QDiary diary = QDiary.diary;
    private final QKeyword keyword = QKeyword.keyword;
    private final QDiaryKeyword diaryKeyword = QDiaryKeyword.diaryKeyword;

    @Override
    public List<Tuple> getMostPopularKeywords() {
        return queryFactory
            .select(keyword, diaryKeyword.count())
            .from(diaryKeyword)
            .join(keyword).on(diaryKeyword.keywordId.eq(keyword))
            .groupBy(keyword.keywordId)
            .orderBy(diaryKeyword.count().desc())
            .limit(5)
            .fetch();
    }

    @Override
    public List<KeywordAdminDto> findKeywordsByType(String keywordType) {
        List<KeywordType> matchedTypes = Arrays.stream(KeywordType.values())
            .filter(e -> e.name().startsWith(keywordType))
            .collect(Collectors.toList());

        return queryFactory
            .select(Projections.constructor(
                KeywordAdminDto.class,
                keyword.keywordId,
                keyword.name,
                keyword.isUse,
                keyword.type,
                Expressions.numberTemplate(Integer.class, "coalesce(count({0}), 0)", diaryKeyword.diaryKeywordId)
            ))
            .from(keyword)
            .leftJoin(diaryKeyword).on(diaryKeyword.keywordId.eq(keyword))
            .where(keyword.type.in(matchedTypes))
            .groupBy(keyword.keywordId)
            .orderBy(diaryKeyword.count().desc())
            .fetch();
    }

    @Override
    public List<Integer> activeKeywords(List<Integer> keywordIds) {
        if (keywordIds == null || keywordIds.isEmpty()) {
            return List.of();
        }

        queryFactory
            .update(keyword)
            .set(keyword.isUse, true)
            .where(keyword.keywordId.in(keywordIds))
            .execute();

        return keywordIds;
    }

    @Override
    public List<Integer> nonActiveKeywords(List<Integer> keywordIds) {
        if (keywordIds == null || keywordIds.isEmpty()) {
            return List.of();
        }

        queryFactory
            .update(keyword)
            .set(keyword.isUse, false)
            .where(keyword.keywordId.in(keywordIds))
            .execute();

        return keywordIds;
    }

    /** 시작일과 마지막일을 기준으로 해당기간동안 특정유저의 일기에서 가장 많이 사용된 키워드 5개를 반환합니다. */
    @Override
    public List<Tuple> findTop5KeywordsByUserIdAndDate(String userId, LocalDateTime start, LocalDateTime end) {

        return queryFactory
            .select(keyword.name, diaryKeyword.count())
            .from(diaryKeyword)
            .join(diaryKeyword.diaryId, diary)
            .join(diaryKeyword.keywordId, keyword)
            .where(
                diary.member.userId.eq(userId),
                diary.createdAt.between(start, end),
                diary.isUse.isTrue(),
                keyword.isUse.isTrue()
            )
            .groupBy(keyword.keywordId, keyword.name)
            .orderBy(diaryKeyword.count().desc())
            .limit(5)
            .fetch();
    }
}
