package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.Diary;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Integer>, DiaryRepositoryCustom {
    List<Diary> findByMemberUserIdAndDateBetweenAndIsUseTrueOrderByDateAsc(String userId, LocalDate start, LocalDate end);
    List<Diary> findByMemberUserIdAndDateAndIsUseTrue(String userId, LocalDate date);

    Integer countByCreatedAtBetweenAndIsUseTrue(LocalDateTime startDateTime, LocalDateTime endDateTime);
    Integer countByMemberUserIdAndCreatedAtBetweenAndIsUseTrue(String userId, LocalDateTime start, LocalDateTime end);

    boolean existsByMember_UserIdAndDateAndIsUse(String userId, LocalDate targetDate, boolean isUse);
}
