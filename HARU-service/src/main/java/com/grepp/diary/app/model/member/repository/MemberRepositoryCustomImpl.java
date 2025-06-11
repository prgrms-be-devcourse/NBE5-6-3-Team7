package com.grepp.diary.app.model.member.repository;

import com.grepp.diary.app.model.member.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QMember member = QMember.member;

    @Override
    public int updateEmail(String userId, String email) {
        long updated = queryFactory
            .update(member)
            .set(member.email, email)
            .where(member.userId.eq(userId))
            .execute();

        return (int) updated;
    }

    @Override
    public int updatePassword(String userId, String password) {
        long updated = queryFactory
            .update(member)
            .set(member.password, password)
            .where(member.userId.eq(userId))
            .execute();
        return (int) updated;
    }


}
