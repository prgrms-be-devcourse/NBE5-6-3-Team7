package com.grepp.diary.app.model.reply.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyAdminDto {
    private String userId;
    private Integer diaryId;
    private LocalDate date;
    private LocalDateTime diaryCreatedAt;
    private LocalDateTime replyCreatedAt;
    private Boolean isReply;
}
