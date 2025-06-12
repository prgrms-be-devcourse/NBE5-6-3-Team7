package com.grepp.mail.app.model

import com.grepp.mail.app.controller.payload.SmtpRequest
import com.grepp.mail.app.model.code.MailTemplatePath
import com.grepp.mail.infra.mail.MailTemplate
import com.grepp.mail.infra.mail.SmtpDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MailService(
    private val template: MailTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val semaphore = Semaphore(3)

    fun send(request: SmtpRequest) = runBlocking {
        request.to.forEach {
            launch(Dispatchers.IO) {
                val dto = SmtpDto(
                    from = request.from,
                    subject = request.subject,
                    to = it,
                    properties = request.properties,
                    templatePath = MailTemplatePath.valueOf(request.eventType.uppercase()).path
                )
                semaphore.withPermit {
                    template.send(dto)
                }
            }
        }
    }
}