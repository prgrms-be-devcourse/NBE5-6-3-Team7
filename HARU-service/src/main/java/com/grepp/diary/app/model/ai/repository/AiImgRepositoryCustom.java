package com.grepp.diary.app.model.ai.repository;

import com.grepp.diary.app.model.ai.dto.AiImgDto;
import com.grepp.diary.app.model.ai.dto.AiWithPathDto;
import java.util.List;

public interface AiImgRepositoryCustom {
    void makeRestImgFalse(Integer aiId);
    List<AiWithPathDto> getAiImgInfoActivated();
}
