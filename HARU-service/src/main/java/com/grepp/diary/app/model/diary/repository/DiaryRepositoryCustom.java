package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.dto.DiaryEmotionStatsDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.reply.dto.ReplyAdminDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface DiaryRepositoryCustom {
    // 일기들을 포함된 이미지와 함께 반환합니다.
    List<Diary> findRecentDiariesWithImages(String userId, Pageable pageable);
    List<Diary> findRecentDiariesHavingImages(String userId, Pageable pageable);
    List<Object []> findDateAndEmotionByUserIdAndYear(String userId, int year);
    List<Object []> findEmotionCountByUserIdAndMonth(String userId, int month);
    List<Object []> findEmotionCountByUserIdAndYear(String userId, int year);

    // 일기, 이미지, 키워드, reply를 모두 조회
    Optional<Diary> findActiveDiaryByDateWithAllRelations(String userId, LocalDate targetDate);
    Optional<Diary> findActiveDiaryByDiaryIdWithAllRelations(String userId, Integer diaryId);

    void deactivateDiaryByDiaryId(Integer id);

    List<DiaryEmotionStatsDto> findEmotionStatsByUserIdAndDate(String userId, LocalDate start, LocalDate end);

    List<ReplyAdminDto> findByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status);
}
