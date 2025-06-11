package com.grepp.diary.app.model.ai.repository;

import com.grepp.diary.app.model.ai.entity.QAi;
import com.grepp.diary.app.model.ai.entity.QAiImg;
import com.grepp.diary.app.model.custom.entity.QCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AiImgRepositoryImpl implements AiImgRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final QAiImg aiImg = QAiImg.aiImg;

    @Override
    public void makeRestImgFalse(Integer aiId) {
        queryFactory
            .update(aiImg)
            .set(aiImg.isUse, false)
            .where(aiImg.ai.aiId.eq(aiId))
            .execute();
    }
}
