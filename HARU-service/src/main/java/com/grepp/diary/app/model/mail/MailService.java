package com.grepp.diary.app.model.mail;

import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.mail.dto.SmtpDto;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.infra.mail.MailApi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailApi mailApi;
    @Value("${app.domain}")
    private String domain;

    private final MemberService memberService;
    private final CustomService customService;

    public void sendMailOnReply(String userId, Object diaryDate) {
        String email = memberService.getEmail(userId);

        SmtpDto smtpDto = new SmtpDto();
        smtpDto.setFrom("haru");
        smtpDto.setTo(List.of(email));
        smtpDto.setSubject("답변 완료 안내");
        Map<String, String> props = new HashMap<>();
        props.put("domain", domain);
        props.put("username", memberService.findById(userId).get().getName());
        props.put("character", customService.findByUserId(userId).get().getAi().getName());
        props.put("date", String.valueOf(diaryDate));
        smtpDto.setProperties(props);
        smtpDto.setEventType("on_reply");

        mailApi.sendOnReply(
            "user-service",
            "ROLE_SERVER",
            smtpDto
        );
    }
}
