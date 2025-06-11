package com.grepp.diary.app.controller.api.diary.payload;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiaryEditRequest {

    private Integer diaryId;
    private String emotion;
    private String content;
    private LocalDate date;
    private List<String> keywords;
    private List<Integer> deletedImageIds;

}
