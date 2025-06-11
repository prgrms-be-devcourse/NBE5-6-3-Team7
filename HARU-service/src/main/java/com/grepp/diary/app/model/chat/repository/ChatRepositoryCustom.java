package com.grepp.diary.app.model.chat.repository;

import java.time.LocalDateTime;

public interface ChatRepositoryCustom {

    Integer getMonthChatCount(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
