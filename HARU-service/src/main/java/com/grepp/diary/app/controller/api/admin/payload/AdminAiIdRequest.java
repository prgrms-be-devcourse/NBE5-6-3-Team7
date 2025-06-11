package com.grepp.diary.app.controller.api.admin.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminAiIdRequest {
    private List<Integer> AiIds;
}
