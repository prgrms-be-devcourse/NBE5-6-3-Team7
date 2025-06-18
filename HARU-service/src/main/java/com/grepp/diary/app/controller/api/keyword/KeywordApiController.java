package com.grepp.diary.app.controller.api.keyword;

import com.grepp.diary.app.model.keyword.KeywordService;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/keyword")
public class KeywordApiController {
    private final KeywordService keywordService;

    @GetMapping("/group")
    public Map<String, List<Keyword>> getAllKeywordsGrouped(){
        return keywordService.findAllGroupedKeyword();
    }
}
