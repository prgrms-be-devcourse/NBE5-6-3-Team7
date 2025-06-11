package com.grepp.diary.app.model.custom.repository;

import com.grepp.diary.app.model.ai.entity.QAi;
import com.grepp.diary.app.model.ai.entity.QAiImg;
import com.grepp.diary.app.model.custom.dto.CustomAIDto;
import com.grepp.diary.app.model.custom.dto.CustomAiInfoDto;
import com.grepp.diary.app.model.custom.entity.QCustom;
import com.grepp.diary.app.model.member.entity.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomRepositoryImpl implements CustomRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QAi ai = QAi.ai;
    private final QCustom custom = QCustom.custom;
    private final QAiImg aiImg = QAiImg.aiImg;

    @Override
    public List<Tuple> getAiByLimit(Integer limit) {
        NumberPath<Long> usageCount = Expressions.numberPath(Long.class, "count");

        JPAQuery<Tuple> query = queryFactory
            .select(ai, custom.count().as(usageCount))
            .from(ai)
            .leftJoin(custom).on(custom.ai.eq(ai))
            .groupBy(ai)
            .orderBy(usageCount.desc());

        if (limit != null && limit > 0) {
            query.limit(limit);
        }

        return query.fetch();
    }

    @Override
    public CustomAIDto getCustomByUserId(String userId) {
        return queryFactory
            .select(Projections.constructor(
                CustomAIDto.class,
                custom.ai.aiId,
                custom.isFormal,
                custom.isLong
            ))
            .from(custom)
            .where(custom.member.userId.eq(userId))
            .fetchOne();

    }

    @Override
    public int updateCustomByUserId(String userId, CustomAIDto customAIDto) {
        long updated = queryFactory
            .update(custom)
            .set(custom.ai.aiId, customAIDto.getAiId())
            .set(custom.isFormal, customAIDto.isFormal())
            .set(custom.isLong, customAIDto.isLong())
            .where(custom.member.userId.eq(userId)).execute();
        return (int) updated;
    }

    @Override
    public Optional<CustomAiInfoDto> getCustomAiInfoByUserId(String userId) {
        return Optional.ofNullable(
            queryFactory
            .select(Projections.constructor(
                CustomAiInfoDto.class,
                ai.name,
                aiImg.savePath,
                aiImg.renamedName
            ))
            .from(custom)
            .leftJoin(custom.ai, ai)
            .leftJoin(ai.images, aiImg)
            .where(custom.member.userId.eq(userId))
            .fetchOne()
        );
    }
}
