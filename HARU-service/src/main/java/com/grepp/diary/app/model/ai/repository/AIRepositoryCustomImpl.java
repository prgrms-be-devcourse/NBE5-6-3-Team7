package com.grepp.diary.app.model.ai.repository;

import com.grepp.diary.app.model.ai.dto.AiInfoDto;
import com.grepp.diary.app.model.ai.entity.QAi;
import com.grepp.diary.app.model.ai.entity.QAiImg;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AIRepositoryCustomImpl implements AIRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QAi ai = QAi.ai;
    QAiImg aiImg = QAiImg.aiImg;

    @Override
    public List<AiInfoDto> getAIListDto() {
        return queryFactory
            .select(Projections.constructor(
                AiInfoDto.class,
                ai.aiId,
                ai.name,
                ai.mbti,
                ai.info,
                aiImg.savePath,
                aiImg.renamedName
            ))
            .from(ai)
            .leftJoin(aiImg)
            .on(ai.aiId.eq(aiImg.ai.aiId))
            .where(ai.isUse.isTrue())
            .fetch();
    }
}
