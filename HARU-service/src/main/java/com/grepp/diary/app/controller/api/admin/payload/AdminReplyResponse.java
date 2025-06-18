package com.grepp.diary.app.controller.api.admin.payload;

import com.grepp.diary.app.model.reply.dto.ReplyAdminDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record AdminReplyResponse(
    List<ReplyInfo> ReplyInfos
) {
    public record ReplyInfo(
        String userId,
        Integer diaryId,
        LocalDate date,
        String diaryCreatedAt,
        String replyCreatedAt,
        Boolean isReply
    ) {
        public static ReplyInfo fromDto(ReplyAdminDto replyAdminDto) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            return new ReplyInfo(
                replyAdminDto.getUserId(),
                replyAdminDto.getDiaryId(),
                replyAdminDto.getDate(),
                replyAdminDto.getDiaryCreatedAt() != null ? replyAdminDto.getDiaryCreatedAt().format(formatter) : null,
                replyAdminDto.getReplyCreatedAt() != null ? replyAdminDto.getReplyCreatedAt().format(formatter) : null,
                replyAdminDto.getIsReply()
            );
        }
    }

    public static AdminReplyResponse fromDtoList(List<ReplyAdminDto> replyAdminDtoList) {
        List<ReplyInfo> replyInfos = replyAdminDtoList.stream().map(ReplyInfo::fromDto).toList();
        return new AdminReplyResponse(replyInfos);
    }
}
