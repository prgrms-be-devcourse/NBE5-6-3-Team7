package com.grepp.diary.app.controller.api.ai.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    private int diaryId;
    private List<Message> chatHistory;
    private String userMessage;
}
