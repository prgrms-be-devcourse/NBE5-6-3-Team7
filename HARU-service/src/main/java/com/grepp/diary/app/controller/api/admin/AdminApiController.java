package com.grepp.diary.app.controller.api.admin;

import com.grepp.diary.app.controller.api.admin.payload.AdminAiIdRequest;
import com.grepp.diary.app.controller.api.admin.payload.AdminAiResponse;
import com.grepp.diary.app.controller.api.admin.payload.AdminAiWriteRequest;
import com.grepp.diary.app.controller.api.admin.payload.AdminKeywordIdRequest;
import com.grepp.diary.app.controller.api.admin.payload.AdminKeywordResponse;
import com.grepp.diary.app.controller.api.admin.payload.AdminKeywordWriteRequest;
import com.grepp.diary.app.model.ai.AiService;
import com.grepp.diary.app.model.ai.dto.AiDto;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.keyword.KeywordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final KeywordService keywordService;
    private final AiService aiService;
    private final CustomService customService;

    @GetMapping("keyword")
    public AdminKeywordResponse getAllKeywords(
        @RequestParam String type
    ) {
        return AdminKeywordResponse.fromDtoList(
            keywordService.findKeywordsByType(type)
        );
    }

    @PatchMapping("keyword/active")
    public List<Integer> modifyKeywordActive(
        @RequestBody AdminKeywordIdRequest request
    ) {
        List<Integer> keywordIds = request.getKeywordIds();

        return keywordService.modifyKeywordActivate(keywordIds);
    }

    @PatchMapping("keyword/nonactive")
    public List<Integer> modifyKeywordNonActive(
        @RequestBody AdminKeywordIdRequest request
    ) {
        List<Integer> keywordIds = request.getKeywordIds();

        return keywordService.modifyKeywordNonActivate(keywordIds);
    }

    @PatchMapping("keyword/modify")
    public Boolean modifyKeyword(
        @RequestBody AdminKeywordWriteRequest keywordWriteRequest
    ) {
        keywordService.modifyKeyword(keywordWriteRequest);

        return true;
    }

    @PostMapping("keyword")
    public Boolean createKeyword(
        @RequestBody AdminKeywordWriteRequest keywordWriteRequest
    ) {
        keywordService.createKeyword(keywordWriteRequest);

        return true;
    }

    /**
     * AI API
     **/
    @GetMapping("ai")
    public AdminAiResponse getAllAis() {
        return AdminAiResponse.fromDtoList(
            customService.getAiByLimit(0)
        );
    }

    @GetMapping("ai/{id}")
    public AiDto getSingleAi(
        @PathVariable Integer id
    ) {
        return aiService.getSingleAi(id);
    }

    @GetMapping("ai/all")
    public List<AiDto> getAllAi() {
        return aiService.getAllAi();
    }

    @PatchMapping("ai/active")
    public List<Integer> modifyAiActive(
        @RequestBody AdminAiIdRequest request
    ) {
        List<Integer> aiIds = request.getAiIds();

        return aiService.modifyAiActivate(aiIds);
    }

    @PatchMapping("ai/nonactive")
    public List<Integer> modifyAiNonActive(
        @RequestBody AdminAiIdRequest request
    ) {
        List<Integer> aiIds = request.getAiIds();

        return aiService.modifyAiNonActivate(aiIds);
    }

    @PatchMapping("ai/modify")
    public Boolean modifyAi(
        @ModelAttribute AdminAiWriteRequest request
    ) {
        aiService.modifyAi(request.getImages(), request);

        return true;
    }

    @PostMapping("ai")
    public Boolean createAi(
        @ModelAttribute AdminAiWriteRequest request
    ) {
        aiService.createAi(request.getImages(), request);

        return true;
    }

}
