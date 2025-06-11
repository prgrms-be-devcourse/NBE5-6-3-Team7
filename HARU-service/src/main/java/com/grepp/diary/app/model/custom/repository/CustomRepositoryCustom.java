package com.grepp.diary.app.model.custom.repository;

import com.grepp.diary.app.model.custom.dto.CustomAIDto;
import com.grepp.diary.app.model.custom.dto.CustomAiInfoDto;
import com.querydsl.core.Tuple;
import java.util.List;
import java.util.Optional;

public interface CustomRepositoryCustom {
    List<Tuple> getAiByLimit(Integer limit);
    CustomAIDto getCustomByUserId(String id);
    int updateCustomByUserId(String userId, CustomAIDto customAIDto);
    Optional<CustomAiInfoDto> getCustomAiInfoByUserId(String userId);
}

