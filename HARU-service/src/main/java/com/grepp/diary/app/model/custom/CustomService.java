package com.grepp.diary.app.model.custom;

import com.grepp.diary.app.model.custom.dto.CustomAiInfoDto;
import com.grepp.diary.app.model.ai.dto.AiWithCountDto;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.ai.entity.AiImg;
import com.grepp.diary.app.model.ai.repository.AiRepository;
import com.grepp.diary.app.model.custom.dto.CustomAIDto;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.custom.repository.CustomRepository;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomService {

    private final CustomRepository customRepository;
    private final ModelMapper mapper;
    private final MemberService memberService;
    private final AiRepository aiRepository;

    public Optional<Custom> findByUserId(String userId) {
        return customRepository.findByMemberUserId(userId);
    }

    public List<AiWithCountDto> getAiByLimit(Integer limit) {
        List<Tuple> result = customRepository.getAiByLimit(limit);

        return result.stream()
            .map(tuple -> {
                Ai ai = tuple.get(0, Ai.class);
                Long count = tuple.get(1, Long.class);

                AiWithCountDto aiDto = mapper.map(ai, AiWithCountDto.class);
                aiDto.setCount(count.intValue());

                return aiDto;
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void registerCustomSettings(String userId, int aiId, boolean isFormal, boolean isLong) {
        Custom custom = new Custom();
        custom.setFormal(isFormal);
        custom.setLong(isLong);

        Member member = memberService.findById(userId)
            .orElseThrow(() -> new CommonException(ResponseCode.MEMBER_NOT_FOUND));
        custom.setMember(member);

        Ai ai = aiRepository.findById(aiId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "해당 ID의 AI가 존재하지 않습니다."));
        custom.setAi(ai);

        customRepository.save(custom);
    }

    public CustomAIDto getCustomAiByUserId(String id) {
        return customRepository.getCustomByUserId(id);
    }

    @Transactional
    public boolean updateCustom(String userId, CustomAIDto customAIDto) {
        return customRepository.updateCustomByUserId(userId, customAIDto) > 0;
    }

    public CustomAiInfoDto findAiAvatarByUserId(String userId) {
        return customRepository.getCustomAiInfoByUserId(userId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "회원의 AI가 존재하지 않습니다."));
    }
}
