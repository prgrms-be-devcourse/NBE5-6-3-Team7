package com.grepp.diary.app.model.member.repository;

import com.grepp.diary.app.model.member.entity.Member;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, String>, MemberRepositoryCustom {

    Integer countByEnabledTrue();

    Optional<Member> findByEmail(String email);

    boolean existsByUserIdAndEmail(String userId, String email);

    Optional<Member> findByUserIdAndEmail(String userId, String email);

    Optional<Member> findByUserId(String userId);

    @Modifying
    @Query("UPDATE Member m SET m.enabled = false WHERE m.userId = :userId")
    void updateEnabledByUserId(@Param("userId") String userId);

    Optional<Member> findMemberByEmail(String email);
}
