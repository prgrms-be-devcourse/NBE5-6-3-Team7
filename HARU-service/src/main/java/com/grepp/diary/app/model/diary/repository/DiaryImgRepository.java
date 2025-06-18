package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.DiaryImg;
import feign.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DiaryImgRepository extends JpaRepository<DiaryImg, Long> {

    // 일기 삭제시 이미지를 비활성화
    @Modifying
    @Query("UPDATE DiaryImg d SET d.isUse = false WHERE d.diary.diaryId = :diaryId")
    int deactivateDiaryImgByDiaryId(@Param("diaryId") Integer diaryId);

    // 일기 수정시 이미지를 비활성화할 경우
    @Modifying
    @Query("UPDATE DiaryImg d SET d.isUse = false, d.type = case when d.type = 'THUMBNAIL' THEN 'MEDIUM' ELSE d.type END "
        + "WHERE d.imgId = :deletedImageId")
    void deactivateDiaryImgByDiaryImgId(Integer deletedImageId);

    @Query("SELECT d.originName FROM DiaryImg d WHERE d.diary.diaryId = :diaryId AND d.type = 'THUMBNAIL' AND d.isUse = true")
    Optional<String> findThumbnailOriginalNameByDiaryId(Integer diaryId);

    @Modifying
    @Query("UPDATE DiaryImg d SET d.type = 'THUMBNAIL' WHERE d.diary.diaryId = :diaryId AND d.originName = :fileName AND d.isUse = true")
    void updateImgTypeToThumbnailByFileName(Integer diaryId, String fileName);

    @Modifying
    @Query("UPDATE DiaryImg d SET d.type = 'MEDIUM' WHERE d.diary.diaryId = :diaryId AND d.type = 'THUMBNAIL' AND d.isUse = true")
    void updateThumbnailToMediumByDiaryId(Integer diaryId);
}
