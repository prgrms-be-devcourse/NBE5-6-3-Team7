package com.grepp.diary.app.model.member.repository;

public interface MemberRepositoryCustom {
    int updateEmail(String userId, String email);
    int updatePassword(String userId, String password);
}
