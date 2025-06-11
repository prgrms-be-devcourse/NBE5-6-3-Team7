package com.grepp.diary.app.model.diary.dto;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.entity.Diary;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DiaryDto {

    private Integer diaryId;
    private String userId;
    private Integer replyId;
    private String content;
    private Emotion emotion;

    public static DiaryDto fromEntity(Diary diary) {
        DiaryDto dto = new DiaryDto();
        dto.setDiaryId(diary.getDiaryId());
        dto.setUserId(diary.getMember().getUserId());
        dto.setReplyId(diary.getReply() != null ? diary.getReply().getReplyId() : null);
        dto.setContent(diary.getContent());
        dto.setEmotion(diary.getEmotion());
        return dto;
    }
}
