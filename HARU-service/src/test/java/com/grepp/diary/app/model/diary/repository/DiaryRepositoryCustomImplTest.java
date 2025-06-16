package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.dto.DiaryEmotionStatsDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiaryRepositoryCustomImplTest {

    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    void findEmotionStatsByUserIdAndDate() {
        String userId = "user01";
        LocalDate start = LocalDate.of(2025, 5, 1);
        LocalDate end = LocalDate.of(2025, 5, 31);
        List<DiaryEmotionStatsDto> dtos = diaryRepository.findEmotionStatsByUserIdAndDate(
            userId, start, end);

        for (DiaryEmotionStatsDto dto : dtos) {
            System.out.println(dto);
        }
    }
}