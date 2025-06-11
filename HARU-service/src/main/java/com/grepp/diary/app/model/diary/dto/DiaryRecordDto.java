package com.grepp.diary.app.model.diary.dto;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.reply.entity.Reply;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DiaryRecordDto {

    private Integer diaryId;
    private LocalDate date;
    private LocalDateTime createdAt;
    private Reply reply;
    private String content;
    private Emotion emotion;
    private List<String> keywordNames;
    private List<DiaryImageInfoDto> images;

    public static DiaryRecordDto fromEntity(Diary diary) {
        DiaryRecordDto dto = new DiaryRecordDto();
        dto.setDiaryId(diary.getDiaryId());
        dto.setReply(diary.getReply() != null ? diary.getReply() : null);
        dto.setContent(diary.getContent());
        dto.setEmotion(diary.getEmotion());
        dto.setDate(diary.getDate());
        dto.setCreatedAt(diary.getCreatedAt());

        if (diary.getImages() != null && !diary.getImages().isEmpty()) {
            List<DiaryImageInfoDto> images = diary.getImages().stream()
                                             .map(img -> new DiaryImageInfoDto(
                                                 img.getImgId(),
                                                 "/uploads/" + img.getSavePath() + img.getRenamedName(),
                                                 img.getOriginName()))
                                             .collect(Collectors.toList());
            dto.setImages(images);
        }

        // 키워드 이름만 추출해서 리스트에 담기
        List<String> keywordNames = diary.getKeywords().stream()
                                         .map(dk -> dk.getKeywordId().getName())
                                         .collect(Collectors.toList());
        dto.setKeywordNames(keywordNames);
        return dto;
    }

}
