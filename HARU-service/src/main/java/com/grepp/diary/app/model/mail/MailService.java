package com.grepp.diary.app.model.mail;

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

    public void sendMailOnReply(String userId) {
        String email = memberService.getEmail(userId);

        SmtpDto smtpDto = new SmtpDto();
        smtpDto.setFrom("haru");
        smtpDto.setTo(List.of(email));
        smtpDto.setSubject("답변 완료 안내");
        Map<String, String> props = new HashMap<>();
        props.put("domain", domain);
        props.put("username", "test");
        props.put("character", "test");
        smtpDto.setProperties(props);
        smtpDto.setEventType("on_reply");

        mailApi.sendOnReply(
            "user-service",
            "ROLE_SERVER",
            smtpDto
        );
    }
}
