package com.grepp.diary.app.model.chat.repository;

import com.grepp.diary.app.model.ai.entity.QAi;
import com.grepp.diary.app.model.chat.entity.QChat;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryCustomImpl implements ChatRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QChat chat = QChat.chat;

    @Override
    public Integer getMonthChatCount(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Integer result = queryFactory
            .select(chat.count.sum())
            .from(chat)
            .where(
                chat.modifiedAt.between(startDateTime, endDateTime),
                chat.isUse.eq(true)
            )
            .fetchOne();

        return result != null ? result : 0;
    }
}
