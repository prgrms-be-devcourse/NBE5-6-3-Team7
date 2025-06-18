package com.grepp.diary.app.controller.api.diary.payload;

import com.grepp.diary.app.model.common.code.ImgType;
import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.DiaryImg;
import java.time.LocalDate;
import java.util.List;

public record DiaryCardResponse(
    List<DiaryCard> diaryCards
) {

    public record DiaryCard(
        Integer diaryId,
        LocalDate date,
        LocalDate createdAt,
        Emotion emotion,
        String content,
        String imagePath
    ) {

        public static DiaryCard fromEntity(Diary diary) {
            String imagePath = null;

            if (diary.getImages() != null && !diary.getImages()
                                                   .isEmpty()) {
                imagePath = diary.getImages()
                                 .stream()
                                 .filter(img -> img.getType() == ImgType.THUMBNAIL)
                                 .map(DiaryImg::getRenamedPath)
                                 .findFirst()
                                 .orElse(null);
            }

            return new DiaryCard(
                diary.getDiaryId(),
                diary.getDate(),
                diary.getCreatedAt()
                     .toLocalDate(),
                diary.getEmotion(),
                diary.getContent(),
                imagePath
            );
        }
    }

    public static DiaryCardResponse fromEntityList(List<Diary> diaries) {
        List<DiaryCard> diaryCards = diaries.stream()
                                            .map(DiaryCard::fromEntity)
                                            .toList();
        return new DiaryCardResponse(diaryCards);
    }
}
