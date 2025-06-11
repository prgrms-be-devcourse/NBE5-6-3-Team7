package com.grepp.diary.app.model.reply.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReplyDto {
    private Integer id;
    private Integer diaryId;
    private Integer aiId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isUse;

}
