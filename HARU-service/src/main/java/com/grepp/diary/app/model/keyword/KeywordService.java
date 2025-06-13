package com.grepp.diary.app.model.keyword;

import com.grepp.diary.app.model.keyword.dto.KeywordAdminDto;
import com.grepp.diary.app.model.keyword.dto.KeywordDto;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.grepp.diary.app.model.keyword.repository.KeywordRepository;
import com.querydsl.core.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final ModelMapper mapper;

    public List<KeywordAdminDto> getMostPopular() {
        List<Tuple> result = keywordRepository.getMostPopularKeywords();

        return result.stream()
            .map(tuple -> {
                Keyword keyword = tuple.get(0, Keyword.class);
                Long count = tuple.get(1, Long.class);

                KeywordAdminDto keywordAdminDto = mapper.map(keyword, KeywordAdminDto.class);
                keywordAdminDto.setCount(count.intValue());

                return keywordAdminDto;
            })
            .collect(Collectors.toList());
    }

    public List<Keyword> findAllKeywordEntities() {
        return keywordRepository.findAll();
    }

    public List<KeywordAdminDto> findKeywordsByType(String keywordType) {
        return keywordRepository.findKeywordsByType(keywordType);
    }

    @Transactional
    public List<Keyword> modifyKeyword(List<Keyword> requests) {
        List<Keyword> results = new ArrayList<>();

        for (Keyword request : requests) {

            Optional<Keyword> optionalKeyword = keywordRepository.findById(request.getKeywordId());

            if (optionalKeyword.isEmpty()) {
                throw new RuntimeException("Keyword not found");
            }

            Keyword keyword = optionalKeyword.get();
            keyword.setName(request.getName());
            keyword.setType(request.getType());
            keyword.setIsUse(request.getIsUse());

            results.add(keyword);
        }

        keywordRepository.saveAllAndFlush(results);

        return results;
    }

    @Transactional
    public Keyword createKeyword(Keyword request) {
        Keyword keyword = new Keyword();

        keyword.setName(request.getName());
        keyword.setType(request.getType());
        keyword.setIsUse(request.getIsUse());

        keywordRepository.save(keyword);

        return keyword;
    }

    public List<KeywordDto> getTop5Keywords(String userId, LocalDate start, LocalDate end) {

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();

        List<Tuple> tuples = keywordRepository.findTop5KeywordsByUserIdAndDate(userId, startDateTime, endDateTime);

        return tuples.stream()
            .map(t -> {
                String name = t.get(0, String.class);
                Long count = t.get(1, Long.class);

                return new KeywordDto(name, Math.toIntExact(count));
            })
            .toList();
    }
}
