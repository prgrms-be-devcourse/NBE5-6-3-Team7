package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.DiaryImg;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DiaryImgRepository extends JpaRepository<DiaryImg, Long> {

    @Modifying
    @Query("UPDATE DiaryImg d SET d.isUse = false WHERE d.diary.diaryId = :diaryId")
    int deactivateDiaryImgByDiaryId(@Param("diaryId") Integer diaryId);
}
