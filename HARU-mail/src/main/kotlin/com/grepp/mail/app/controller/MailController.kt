package com.grepp.mail.app.controller

import com.grepp.mail.app.controller.payload.SmtpRequest
import com.grepp.mail.app.model.MailService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("mail")
// TODO 서버 권한 검증 필요시 @PreAuthorize
class MailController(
    private val mailService: MailService
) {

    @PostMapping("send")
    fun send(
        @RequestBody
        @Valid
        request: SmtpRequest
    ){
        mailService.send(request)
    }
}