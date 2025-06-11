package com.grepp.diary.app.controller.api.custom;

import com.grepp.diary.app.controller.api.custom.payload.CustomResponse;
import com.grepp.diary.app.model.custom.CustomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/custom")
public class CustomApiController {

    private final CustomService customService;

    @GetMapping
    public CustomResponse getCustomAiByUserId(
        Authentication authentication
    ) {
        String userId = authentication.getName();
        return CustomResponse.fromDto(customService.getCustomAiByUserId(userId));
    }
}
