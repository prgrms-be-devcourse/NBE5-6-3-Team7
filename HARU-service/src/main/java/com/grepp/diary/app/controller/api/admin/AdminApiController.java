package com.grepp.diary.app.controller.api.admin;

import com.grepp.diary.app.controller.api.admin.payload.AdminAiStatusRequest;
import com.grepp.diary.app.controller.api.admin.payload.AdminAiResponse;
import com.grepp.diary.app.controller.api.admin.payload.AdminAiWriteRequest;
import com.grepp.diary.app.controller.api.admin.payload.AdminKeywordResponse;
import com.grepp.diary.app.model.ai.AiService;
import com.grepp.diary.app.model.ai.dto.AiDto;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.keyword.KeywordService;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @PatchMapping("keyword")
    public ResponseEntity<List<Keyword>> modifyKeyword(
        @RequestBody List<Keyword> requests
    ) {
        List<Keyword> results = keywordService.modifyKeyword(requests);

        return ResponseEntity.ok(results);
    }

    @PostMapping("keyword")
    public ResponseEntity<Keyword> createKeyword(
        @RequestBody Keyword request
    ) {
        Keyword result = keywordService.createKeyword(request);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("keyword/delete")
    public ResponseEntity<List<Keyword>> deleteKeyword(
        @RequestBody List<Integer> requests
    ) {
        List<Keyword> deleteKeywords = keywordService.deleteKeyword(requests);

        return ResponseEntity.ok(deleteKeywords);
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

    @PostMapping("ai")
    public ResponseEntity<AiDto> createAi(
        @ModelAttribute AdminAiWriteRequest request
    ) {
        Ai createdAi = aiService.createAi(request.getImages(), request);
        AiDto response = AiDto.fromEntity(createdAi);

        return ResponseEntity.ok(response);
    }

    @GetMapping("ai/{id}")
    public AiDto getSingleAi(
        @PathVariable Integer id
    ) {
        return aiService.getSingleAi(id);
    }

    @PatchMapping("ai")
    public ResponseEntity<AiDto> modifyAi(
        @ModelAttribute AdminAiWriteRequest request
    ) {
        Ai modifiedAi = aiService.modifyAi(request.getImages(), request);
        AiDto response = AiDto.fromEntity(modifiedAi);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/ai/status")
    public ResponseEntity<List<AiDto>> modifyAiStatus(
        @RequestBody AdminAiStatusRequest request
    ) {
        List<Integer> aiIds = request.getAiIds();
        Boolean status = request.getIsUse();

        List<Ai> modifiedAis;

        if (Boolean.TRUE.equals(status)) {
            modifiedAis = aiService.modifyAiActivate(aiIds);
        } else {
            modifiedAis = aiService.modifyAiNonActivate(aiIds);
        }

        List<AiDto> response = modifiedAis.stream()
            .map(AiDto::fromEntity)
            .toList();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("ai/delete")
    public ResponseEntity<List<AiDto>> deleteAi(
        @RequestBody List<Integer> requests
    ) {
        List<Ai> deletedAis = aiService.deleteAi(requests);
        List<AiDto> response = deletedAis.stream()
            .map(AiDto::fromEntity)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


}
