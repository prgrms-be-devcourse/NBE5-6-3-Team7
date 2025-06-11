package com.grepp.mail.infra.mail

import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine

@Component
class MailTemplate(
    private val javaMailSender: JavaMailSender,
    private val templateEngine: TemplateEngine
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun send(dto: SmtpDto) {

    }
}