package com.grepp.diary.infra.mail;

import com.grepp.diary.app.model.member.dto.SmtpDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "mail-service",
    url = "http://localhost:8081/mail/"
)
public interface MailApi {

    @PostMapping("verify")
    void sendMail(
        @RequestHeader(name = "x-member-id") String userId,
        @RequestHeader(name = "x-member-role") String role,
        @RequestBody SmtpDto payload
    );
}
