package com.grepp.mail.infra.mail

import jakarta.mail.Message
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class MailTemplate(
    private val javaMailSender: JavaMailSender,
    private val templateEngine: TemplateEngine
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun send(dto: SmtpDto) {
        withContext(Dispatchers.IO) {
            javaMailSender.send(MimeMessagePreparator { mimeMessage: MimeMessage ->
                mimeMessage.setFrom(dto.from)
                mimeMessage.addRecipients(Message.RecipientType.TO, dto.to)
                mimeMessage.subject = dto.subject
                mimeMessage.setText(render(dto), "UTF-8", "html")
            })
        }
    }

    private fun render(dto: SmtpDto): String {
        val context = Context()
        context.setVariables(dto.properties)
        return templateEngine.process(dto.templatePath, context)
    }
}