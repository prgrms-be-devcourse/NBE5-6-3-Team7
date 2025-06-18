package com.grepp.diary.app.model.diary;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiaryServiceTest {

    @Autowired
    private DiaryService diaryService;

    @Test
    void getEmotionStats() {
        String userId = "user01";
        LocalDate date = LocalDate.of(2025, 5, 20);
        String stats = diaryService.getEmotionStats(userId, date);
        System.out.println(stats);
    }
}