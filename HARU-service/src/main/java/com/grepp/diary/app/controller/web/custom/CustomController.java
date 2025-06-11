package com.grepp.diary.app.controller.web.custom;

import com.grepp.diary.app.controller.web.custom.form.SettingAiForm;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.custom.dto.CustomAIDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("custom")
public class CustomController {

    private final CustomService customService;

    @PostMapping("/update-custom")
    public String updateAiSettings(
        @ModelAttribute("aiForm")   SettingAiForm settingAiForm,
        Authentication authentication,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/app/settings/ai";
        }

        log.info(settingAiForm.toString());

        String userId = authentication.getName();

        boolean isSuccess = customService.updateCustom(userId, new CustomAIDto(
            settingAiForm.getSelectedAiId(), settingAiForm.getIsFormal(), settingAiForm.getIsLong()
        ));
        if (!isSuccess) {
            redirectAttributes.addFlashAttribute("message", "AI 설정 도중 문제가 발생하였습니다");
            return "redirect:/app/settings/ai";
        }

        redirectAttributes.addFlashAttribute("message", "성공적으로 AI 설정을 변경하였습니다");
        return "redirect:/app/settings";
    }
}
