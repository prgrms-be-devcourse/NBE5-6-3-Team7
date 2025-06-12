package com.grepp.mail.infra.mail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
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
            javaMailSender.send(MimeMessagePreparator { mimeMessage ->
                val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

                helper.setFrom(dto.from)
                helper.setTo(dto.to)
                helper.setSubject(dto.subject)
                helper.setText(render(dto), true)

                helper.addInline("mail-image", ClassPathResource("static/images/login/opened-mail.png")
                );
            })
        }
    }

    private fun render(dto: SmtpDto): String {
        val context = Context()
        context.setVariables(dto.properties)
        return templateEngine.process(dto.templatePath, context)
    }
}