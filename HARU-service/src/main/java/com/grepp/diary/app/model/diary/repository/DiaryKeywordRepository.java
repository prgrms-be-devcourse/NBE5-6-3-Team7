package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.keyword.entity.DiaryKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryKeywordRepository extends JpaRepository<DiaryKeyword, Long> {
    void deleteByDiaryId(Diary diaryId);
}
